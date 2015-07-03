package controllers

import models.User
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import views.html

/**
 * Created by murat on 7/2/15.
 */
object Auth extends Controller{

  val loginForm = Form(
    mapping(
      "id"       -> ignored[Option[Int]](None),
      "username" -> text,
      "password" -> text
    )(User.apply) (User.unapply)
      verifying ("Invalid username or password", result => result match {
        case User(id, username, password) => check(username, password)
      })
    )

  def check(username: String, password: String) = {
    username == "admin" && password == "1234"
  }

  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors)),
      user => Redirect(routes.Application.index()).withSession(Security.username -> user.username)
    )
  }

  def logout = Action {
    Redirect(routes.Auth.login()).withNewSession.flashing(
      "success" -> "You are now logged out."
    )
  }

}

//trait Secured {
//
//  def username(request: RequestHeader) = request.session.get(Security.username)
//
//  def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Auth.login)
//
//  def withAuth(f: => String => Request[AnyContent] => Result) = {
//    Security.Authenticated(username, onUnauthorized) { user =>
//      Action(request => f(user)(request))
//    }
//  }
//
//  /**
//   * This method shows how you could wrap the withAuth method to also fetch your user
//   * You will need to implement UserDAO.findOneByUsername
//   */
//  def withUser(f: User => Request[AnyContent] => Result) = withAuth { username => implicit request =>
//    User.findOneByUsername(username).map { user =>
//      f(user)(request)
//    }.getOrElse(onUnauthorized(request))
//  }
//}