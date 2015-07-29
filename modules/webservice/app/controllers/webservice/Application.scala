package controllers.webservice

import models.admin.{Question, QuestionTable}
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.lifted.TableQuery

/**
 * Created by Murat.
 */
object Application extends Controller{

  lazy val db = Database.forDataSource(DB.getDataSource())
  lazy val questions = TableQuery[QuestionTable]

  def index = Action{
    implicit val qFormat = Json.format[Question]

    val data = db.withSession { implicit session => questions.list }
    
    Ok(Json.obj("users" -> data.toList))
  }

}
