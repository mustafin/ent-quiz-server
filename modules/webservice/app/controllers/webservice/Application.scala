package controllers.webservice

import helpers.Token
import models.admin.{Question, QuestionTable}
import models.webservice.{GameUserDAO, GameUser}
import play.api.Play
import play.api.Play.current
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{Reads, JsPath, JsError, Json}
import play.api.mvc.{Action, Controller}
import slick.driver.JdbcProfile

import scala.slick.driver.MySQLDriver.simple._

import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax

/**
 * Created by Murat.
 */
object Application extends Controller{

  lazy val db = DatabaseConfigProvider.get[JdbcProfile](Play.current).db

  implicit val uFormat = Json.format[GameUser]

  implicit val userReads = (
    (JsPath \ "username").read[String] and
      (JsPath \ "password").read[String]
    ).tupled

  def register = Action(parse.json){ request =>
    request.body.validate[(String, String)].map{
      case (username: String, password: String) =>
        val user = GameUser(None, username, password, Some(1200))
        GameUserDAO.register(user)
        Ok(Json.obj("success" -> 1))
    }.recoverTotal{
      e => BadRequest(JsError.toJson(e))
    }
  }

  def login = Action(parse.json){ request =>
    request.body.validate[(String, String)].map{
      case (username: String, password: String) =>
        val exist = GameUserDAO.checkCredentials(username, password)

        if(exist) {
          val user = GameUserDAO.findByName(username)
          val token = Token.createToken(user.get)
          Ok(Json.obj("success" -> 1, "token" -> token, "userId" -> user.get.id))
        }else {
          Unauthorized(Json.obj("error" -> "wrong username or password"))
        }
    }.recoverTotal{
      e => BadRequest(JsError.toJson(e))
    }
  }

}
