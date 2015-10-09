package controllers.admin

import javax.inject.Inject

import controllers.admin
import models.admin.{User, UserDAO}
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import views.html
import play.api.i18n.Messages.Implicits._

/**
 * Created by murat on 7/2/15.
 */
class Auth @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport{


  val loginForm = Form(
    mapping(
      "id"       -> ignored[Option[Long]](None),
      "username" -> text,
      "password" -> text
    )(User.apply) (User.unapply)
      verifying ("Invalid username or password", result => result match {
        case User(id, username, password) => check(username, password)
      })
    )

  def check(username: String, password: String) = {
    UserDAO.checkCredentials(username, password)
  }

  def login = Action { implicit request =>
    Ok(html.admin.login(loginForm))
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.admin.login(formWithErrors)),
      user => Redirect(admin.routes.Application.index()).withSession(Security.username -> user.username)
    )
  }

  def logout = Action {
    Redirect(routes.Auth.login()).withNewSession.flashing(
      "success" -> "You are now logged out."
    )
  }

}

trait Secured {

  case class AuthenticatedRequest[A](
                                      user: User, request: Request[A]
                                      ) extends WrappedRequest(request)

  def Authenticated[A](p: BodyParser[A])(f: AuthenticatedRequest[A] => Result) = {
    Action(p) { request =>
      request.session.get(Security.username).flatMap(u => UserDAO.findByName(u)).map { user =>
        f(AuthenticatedRequest(user, request))
      }.getOrElse(onUnauthorized(request))
    }
  }

  // Overloaded method to use the default body parser
  import play.api.mvc.BodyParsers._
  def Authenticated(f: AuthenticatedRequest[AnyContent] => Result): Action[AnyContent]  = {
    Authenticated(parse.anyContent)(f)
  }

  def username(request: RequestHeader) = request.session.get(Security.username)

  def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Auth.login())

  def withAuth(f: => String => Request[AnyContent] => Result) = {
    Security.Authenticated(username, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }
  }

  /**
   * This method shows how you could wrap the withAuth method to also fetch your user
   * You will need to implement UserDAO.findOneByUsername
   */
  def withUser[A](f: User => Request[AnyContent] => Result) = withAuth { username => implicit request =>
    UserDAO.findByName(username).map { user =>
      f(user)(request)
    }.getOrElse(onUnauthorized(request))
  }
}