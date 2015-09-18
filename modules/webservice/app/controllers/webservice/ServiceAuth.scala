package controllers.webservice

import controllers.admin.routes
import helpers.Token
import models.webservice.{GameUserDAO, GameUser}
import play.api.libs.json.Json
import play.api.mvc._

/**
 * Created by Murat.
 */
trait ServiceAuth {

  case class ServiceAuthRequest[A](
                                      user: GameUser, request: Request[A]
                                      ) extends WrappedRequest(request)

  def Authenticated[A](p: BodyParser[A])(f: ServiceAuthRequest[A] => Result) = {
    Action(p) { request =>
      val user = request.headers.get("Authorization").map(
        st => st.split(" ") match {
          case Array(a, b) if a == "Bearer" =>
            GameUserDAO.fromJsObj(Token.decodeToken(b))
          case _ => None
        }
      )
      val p = user match {
        case Some(Some(u)) =>
          Some(f(ServiceAuthRequest(u, request)))
        case _ => None
      }
      p.getOrElse(Results.Unauthorized(Json.obj("error" -> "unauthorized")))
    }
  }

  import play.api.mvc.BodyParsers._
  def Authenticated(f: ServiceAuthRequest[AnyContent] => Result): Action[AnyContent]  = {
    Authenticated(parse.anyContent)(f)
  }


}
