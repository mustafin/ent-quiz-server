package controllers.webservice

import gameservice.{GameServiceImpl, GameService}
import helpers.Push
import models.webservice.GameDAO.Implicits._
import models.webservice._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.mvc.{Result, Request, Controller}

import scala.concurrent.Future

/**
 * Created by Murat.
 */
object InviteController extends Controller with ServiceAuth{

  def findUser() = Authenticated.async(parse.json){ case AuthReq(user, req) =>
    req.body.validate[String]((JsPath \ "username").read[String]).map{
      case username =>
        GameUserDAO.findByName(username).map{
          user => Ok(Json.obj("user" -> user))
        } recover { case cause => BadRequest(Json.obj("error" -> "user not found"))}

    }.recoverTotal{
      e => Future.successful(BadRequest(Json.obj("error" -> "wrong request body format")))
    }
  }

  def inviteForGame() = Authenticated.async(parse.json){ case AuthReq(user, req) =>
    req.body.validate[Long]((JsPath \ "userId").read[Long]).map{
      case userId =>
        GameUserDAO.userDevicesIds(userId).map{
          devicesIds =>
            Push.devPush(s"invite from user ${user.username}", devicesIds)
            Ok(Json.obj("success" -> 1))
        } recover { case cause => BadRequest(Json.obj("error" -> s"no devices registered for userId $userId"))}
    }.recoverTotal{
      e => Future.successful(BadRequest(Json.obj("error" -> "wrong request body format")))
    }
  }

  def startGame() = Authenticated.async(parse.json){ case AuthReq(user, req) =>
    validate[Long](req, "userId"){
      userId => Future.successful(Ok(Json.obj("succes" -> 1)))

    }
  }

  def rejectGame() = Authenticated.async(parse.json){ case AuthReq(user, req) =>
    validate[Long](req, "userId"){
      userId => Future.successful(Ok(Json.obj("succes" -> 1)))
        GameUserDAO.userDevicesIds(userId).map {
          deviceIds =>
            Push.rejectGame(user, deviceIds)
            Ok(Json.obj("success" -> 1))
        } recover { case cause => BadRequest(Json.obj("error" -> s"no devices registered for userId $userId"))}
    }
  }

  def validate[T](req: Request[JsValue], key: String)(body:T => Future[Result]): Future[Result] ={
    req.body.validate[T]((JsPath \ key).read[T]).map{
      case d => body(d)
    }.recoverTotal{
      e => Future.successful(BadRequest(Json.obj("error" -> "wrong request body format")))
    }
  }

}
