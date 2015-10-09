package controllers.webservice

import models.admin.{Question, Category, Answer}
import models.webservice._
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsError, Json, JsObject, JsValue}
import play.api.mvc.{Result, Action, Controller}
import play.api.mvc.Controller
import play.api.Play.current
import slick.driver.JdbcProfile
import play.api.libs.concurrent.Execution.Implicits._
import models.webservice.GameDAO.Implicits._
import models.webservice.RoundDAO.Implicits._

import scala.concurrent.Future
import scala.slick.driver.MySQLDriver.simple._


/**
 * Created by Murat.
 */
object GameController extends Controller with ServiceAuth{

  lazy val games = TableQuery[GameTable]
  lazy val db = DatabaseConfigProvider.get[JdbcProfile](Play.current).db

  def index: Action[JsValue] = Authenticated(parse.json){ req =>
    Ok(Json.obj("asd" -> "asf"))
  }

  def start = Authenticated.async{ req =>
    val gameFuture: Future[JsValue] = GameDAO.newGame(req.user.get).map(Json.toJson(_))
    val catFuture: Future[JsValue] = GameDAO.moveData().map(Json.toJson(_))
    for{
      game <- gameFuture
      cat <- catFuture
    }yield {
      Ok(game.as[JsObject] + ("data" -> cat))
    }
  }

  //{"game":"2","round":"1","cat":"3","questions":[{"qid":"1","aid":"2"},{"qid":"5","aid":"3"},{"qid":"2","aid":"1"}]}
  def submitRound = Authenticated.async(parse.json){ req =>
    req.request.body.validate[Round].map{
      case round: Round =>
        RoundDAO.add(round)
        val catFuture: Future[JsValue] = GameDAO.moveData().map(Json.toJson(_))
        catFuture.map(x => Ok(Json.obj("data" -> x)))
    }.recoverTotal(
      e => Future.successful(BadRequest(Json.obj("error" -> "Wrong format")))
    )
  }

  def test = Action.async{ req =>
    val game = GameDAO.moveData()
    game.map(
      x =>
        Ok(Json.toJson(x))
    )

  }

}
