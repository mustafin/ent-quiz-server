package controllers.webservice

import helpers.Token
import models.webservice.{GameUserDAO, GameUser}
import play.api.libs.json.Json
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future
import scala.util.{Success, Failure, Try}

/**
 * Created by Murat.
 */
trait ServiceAuth {

  case class AuthReq[+A](user: GameUser, request: Request[A]) extends WrappedRequest(request)

  object Authenticated extends ActionBuilder[AuthReq]{
    override def invokeBlock[A](request: Request[A], block: (AuthReq[A]) => Future[Result]): Future[Result] = {
      val user = request.headers.get("Authorization").map(
        st => st.split(" ") match {
          case Array(a, b) if a == "Bearer" =>
            Token.decodeToken(b) match {
              case Success(obj) => Right(GameUserDAO.fromJsObj(obj))
              case Failure(e) => Left(Results.Unauthorized(Json.obj("error" -> "wrong username or password")))
            }
          case _ => Left(Results.BadRequest(Json.obj("error" -> "invalid header format")))
        }
      ).getOrElse(Left(Results.BadRequest(Json.obj("error" -> "no authorization header"))))
      user match {
        case Right(Some(u)) =>
          block(AuthReq(u, request))
        case Left(e) => Future.successful(e)
      }
    }

  }

}
