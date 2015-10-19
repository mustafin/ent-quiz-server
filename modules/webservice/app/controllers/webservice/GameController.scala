package controllers.webservice

import gameservice.GameService
import models.admin.{Question, Category, Answer}
import models.webservice._
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsError, Json, JsObject, JsValue}
import play.api.mvc.{Result, Action, Controller}
import play.api.mvc.Controller
import slick.driver.JdbcProfile
import play.api.libs.concurrent.Execution.Implicits._
import models.webservice.GameDAO.Implicits._
import models.webservice.RoundDAO.Implicits._

import scala.concurrent.Future
import scala.slick.driver.MySQLDriver.simple._
import scala.util.{Failure, Success}

/**
 * Created by Murat.
 */
object GameController extends Controller with ServiceAuth {

  lazy val games = TableQuery[GameTable]
  lazy val db = DatabaseConfigProvider.get[JdbcProfile](Play.current).db

  def index: Action[JsValue] = Authenticated(parse.json) { req =>
    Ok(Json.obj("asd" -> "asf"))
  }

  def start = Authenticated.async { req =>
    for {
      game <- GameService.startGame(req.user)
      (rId, cat) <- GameService.getRoundData(req.user, game)
    } yield {
      Ok(Json.toJson(game.toGameData(req.user)).as[JsObject] + ("roundId" -> Json.toJson(rId)) + ("data" -> Json.toJson(cat)))
    }
  }

  def submitRound = Authenticated(parse.json) { req =>
    req.request.body.validate[GameRound].map {
      case round: GameRound =>
        GameService.submitRound(round, req.user)
        Ok(Json.obj("success" -> 1))
    }.recoverTotal(
      e => BadRequest(Json.obj("error" -> "wrong request body format"))
    )
  }

  def getRoundData(id: Long) = Authenticated.async { authReq =>
    val game = GameDAO.find(Some(id))

    game.flatMap {
      case Some(g) =>
        val game = g.toGameData(authReq.user)
        val data = GameService.getRoundData(authReq.user, g)
        data.map{ case (rId, d) =>
          Ok(Json.toJson(game).as[JsObject] + ("roundId" -> Json.toJson(rId)) + ("data" -> Json.toJson(d)))}
      case None =>
        Future.successful(BadRequest(Json.obj("error" -> "Wrong game id")))
    }

  }

  def clearGames = Action.async{
    GameDAO.tempClear.map{
      x => Ok("games cleared")
    }.recover{
      case e => BadRequest(e.getMessage)
    }

  }

  //  implicit def conv(stat: Result): Future[Result] = Future.successful(stat)


}


