package controllers.webservice

import gameservice.GameServiceImpl
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
object GameController extends Controller with ServiceAuth with GameServiceImpl {

  def index: Action[JsValue] = Authenticated(parse.json) { req =>
    Ok(Json.obj("asd" -> "asf"))
  }

  def test = Action.async{ req =>
    startGameOrJoin(GameUser(Some(2), "askar", "", Some(1200)))
      .map(Ok(_))
  }

  def start = Authenticated.async { req =>
    startGameOrJoin(req.user).map(Ok(_))
    .recover{
      case er =>
        println(er.getMessage)
        BadRequest(Json.obj("error" -> "unexpected error"))
    }
  }

  def submitRound = Authenticated.async(parse.json) { req =>
    req.request.body.validate[GameRound].map {
      case round: GameRound =>
        submitRound(round, req.user).map{
          _ => Ok(Json.obj("success" -> 1))
        } recover {
          case e => BadRequest(Json.obj("error" -> e.getMessage))
        }
      case _ => Future.successful(BadRequest(Json.obj("error" -> "wrong request body format")))
    }.recoverTotal(
      e => Future.successful(BadRequest(Json.obj("error" -> "wrong request body format")))
    )
  }

  def getRoundData(id: Long) = Authenticated.async { authReq =>
    getRoundData(authReq.user, id)
      .map(Ok(_)) recover { case cause => BadRequest(Json.obj("error" -> "wrong id"))}
  }

  def clearGames = Action.async{
    GameDAO.tempClear.map{
      x => Ok("games cleared")
    }.recover{
      case e => BadRequest(e.getMessage)
    }
  }

  def newStart = Authenticated.async{ req =>
    startGameOrJoin(req.user).map(Ok(_)) recover {
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


