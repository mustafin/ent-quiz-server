package helpers

import io.really.jwt.{JWTResult, JWT}
import models.webservice.{GameUserDAO, GameUser}
import play.api.libs.json.{JsObject, Json}
import models.webservice.GameUserDAO._

import scala.util.{Failure, Success, Try}

/**
 * Created by Murat.
 */
object Token {

  final val key = "qFbR3CW6"

  def createToken(user: GameUser): String ={
    val payload = Json.toJson(user)
    JWT.encode(key, payload.as[JsObject])
  }

  def decodeToken(token: String): Try[JsObject] ={
    println(s"*$token*")
    val res = JWT.decode(token, Some(key))
    res match {
      case r: JWTResult.JWT => Success(r.payload)
      case JWTResult.TooManySegments => Failure(new Exception("TooManySegments"))
      case JWTResult.NotEnoughSegments => Failure(new Exception("NotEnoughSegments"))
      case JWTResult.EmptyJWT => Failure(new Exception("EmptyJWT"))
      case JWTResult.InvalidSignature => Failure(new Exception("InvalidSignature"))
      case JWTResult.InvalidHeader => Failure(new Exception("InvalidHeader"))
    }
  }

}
