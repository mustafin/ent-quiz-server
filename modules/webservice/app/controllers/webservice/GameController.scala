package controllers.webservice

import gameservice.{GameServiceImpl, GameService}
import helpers.Push
import models.webservice.GameDAO.Implicits._
import models.webservice._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future
import scala.slick.driver.MySQLDriver.simple._

/**
 * Created by Murat.
 */
object GameController extends Controller with ServiceAuth {

  def index: Action[JsValue] = Authenticated(parse.json) { req =>
    Ok(Json.obj("asd" -> "asf"))
  }

  def start = Authenticated.async { req =>
    for {
      game <- GameService.startGame(req.user)
      (rId, cat) <- GameService.getRoundData(req.user, game)
      gameData <- game.toGameData(req.user)
    } yield {
      Ok(Json.toJson(gameData).as[JsObject] + ("roundId" -> Json.toJson(rId)) + ("data" -> Json.toJson(cat)))
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

    val f = for{
      g <- GameDAO.find(Some(id))
      if g.isDefined
      game <- g.get.toGameData(authReq.user)
      (rId, d) <- GameService.getRoundData(authReq.user, g.get)
    } yield {
      Ok(Json.toJson(game).as[JsObject] + ("roundId" -> Json.toJson(rId)) + ("data" -> Json.toJson(d)))
    }
    f recover { case cause => BadRequest(Json.obj("error" -> "wrong id"))}

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

  def registerDevice() = Authenticated.async(parse.json){ req =>
    implicit val userDeviceForm = (
      (JsPath \ "deviceId").read[String] and
      (JsPath \ "deviceOS").read[String]
      ).tupled
    req.request.body.validate[(String, String)].map {
      case (deviceId, deviceOS) =>
        val uDevice = GameUserDevice(req.user.id.get, deviceId, deviceOS)
        GameUserDAO.registerDevice(uDevice).map{
          _ => Ok(Json.obj("success" -> 1))
        }.recover{case cause => BadRequest(Json.obj("error" -> cause.getMessage))}
    }.recoverTotal(
      e => Future.successful(BadRequest(Json.obj("error" -> "wrong request body format")))
    )

  }

  def findUser() = Authenticated.async(parse.json){ req =>
    req.request.body.validate[String]((JsPath \ "username").read[String]).map{
      case username =>
        GameUserDAO.findByName(username).map{
          user => Ok(Json.obj("user" -> user))
        } recover { case cause => BadRequest(Json.obj("error" -> "user not found"))}

    }.recoverTotal{
      e => Future.successful(BadRequest(Json.obj("error" -> "wrong request body format")))
    }
  }

  def inviteForGame() = Authenticated.async(parse.json){ req =>
    req.request.body.validate[Long]((JsPath \ "userId").read[Long]).map{
      case userId =>
        GameUserDAO.userDevicesIds(userId).map{
          devicesIds =>
            Push.devPush(s"invite from user ${req.user.username}", devicesIds)
            Ok(Json.obj("success" -> 1))
        } recover { case cause => BadRequest(Json.obj("error" -> s"no devices registered for userId $userId"))}
    }.recoverTotal{
      e => Future.successful(BadRequest(Json.obj("error" -> "wrong request body format")))
    }
  }

  //TODO REMOVE
  def testSendMessage() = Action.async{
    val devices = TableQuery[GameUserDevicesTable]
    GameDAO.devices().map{
      d =>
        Push.sendInvite(GameUser(Some(1), "murat", "", Some(214242)), d.map(_.deviceId))
//        Push.devPush("Message TEST", d.map(_.deviceId))
        Ok("OK")
    }
  }

  //  implicit def conv(stat: Result): Future[Result] = Future.successful(stat)


}


