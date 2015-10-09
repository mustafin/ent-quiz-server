package helpers

import io.really.jwt.{JWTResult, JWT}
import models.webservice.{GameUserDAO, GameUser}
import play.api.libs.json.{JsObject, Json}
import models.webservice.GameUserDAO._

/**
 * Created by Murat.
 */
object Token {

  final val key = "qFbR3CW6"

  def createToken(user: GameUser): String ={
    val payload = Json.toJson(user)
    JWT.encode(key, payload.as[JsObject])
  }

  def decodeToken(token: String): JsObject ={
    val res = JWT.decode(token, Some(key)).asInstanceOf[JWTResult.JWT]
    res.payload
  }

}
