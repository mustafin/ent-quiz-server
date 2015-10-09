package controllers.webservice

import controllers.admin.routes
import helpers.Token
import models.webservice.{GameUserDAO, GameUser}
import play.api.libs.json.Json
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

/**
 * Created by Murat.
 */
trait ServiceAuth {

  case class ServiceAuthRequest[+A](
                                      user: Option[GameUser], request: Request[A]
                                      ) extends WrappedRequest(request)

  object Authenticated extends ActionBuilder[ServiceAuthRequest]{
    override def invokeBlock[A](request: Request[A], block: (ServiceAuthRequest[A]) => Future[Result]): Future[Result] = {
      val user = request.headers.get("Authorization").map(
        st => st.split(" ") match {
          case Array(a, b) if a == "Bearer" =>
            GameUserDAO.fromJsObj(Token.decodeToken(b))
          case _ => None
        }
      )
      val p = user match {
        case Some(u)   =>
          Some(block(ServiceAuthRequest(u, request)))
        case _ => None
      }
      p.getOrElse(Future(Results.Unauthorized(Json.obj("error" -> "unauthorized"))))
    }
  }

}
