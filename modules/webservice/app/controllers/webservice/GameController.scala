package controllers.webservice

import gameservice.{GameServiceImpl, GameService}
import models.webservice.GameDAO.Implicits._
import models.webservice._
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.{Action, Controller}
import slick.driver.JdbcProfile

import scala.concurrent.Future
import scala.slick.driver.MySQLDriver.simple._

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

  def submitRound = Authenticated.async(parse.json) { req =>
    req.request.body.validate[GameRound].map {
      case round: GameRound =>
        GameService.submitRound(round, req.user).map{
          x => Ok(Json.obj("success" -> 1))
        }.recover{
          case e => BadRequest(Json.obj("error" -> e.getMessage))
        }
    }.recoverTotal(
      e => Future.successful(BadRequest(Json.obj("error" -> "wrong request body format")))
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

  def newStart = Authenticated.async{ req =>
    GameServiceImpl.startGame(req.user).map(Ok(_)) recover {
      case e => BadRequest(Json.obj("error" -> e.getMessage))
    }

  }


  //  implicit def conv(stat: Result): Future[Result] = Future.successful(stat)


}


