package controllers.admin

import play.api.mvc.Controller

object Application extends Controller with Secured{

  def index() = withAuth{ username => implicit rs =>
    Ok(views.html.admin.index("Your new application is ready."))
  }

}