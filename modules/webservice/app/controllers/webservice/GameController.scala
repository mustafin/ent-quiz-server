package controllers.webservice

import models.webservice.{GameDAO, Game, GameTable}
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import play.api.mvc.{Result, Action, Controller}
import play.api.mvc.Controller
import play.api.Play.current
import slick.driver.JdbcProfile

import scala.slick.driver.MySQLDriver.simple._


/**
 * Created by Murat.
 */
object GameController extends Controller with ServiceAuth{

  lazy val games = TableQuery[GameTable]
  lazy val db = DatabaseConfigProvider.get[JdbcProfile](Play.current).db


  def index = Authenticated(parse.json){ req =>
    Ok(Json.obj("asd" -> "asf"))
  }

  def start = Authenticated(parse.json){ req =>
    GameDAO.newGame(req.user)

    Ok("")
  }

}
