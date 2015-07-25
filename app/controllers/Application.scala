package controllers

import play.api.mvc._

object Application extends Controller with Secured{

  def index = withAuth{ username => implicit rs =>
    Ok(views.html.index("Your new application is ready."))
  }

}