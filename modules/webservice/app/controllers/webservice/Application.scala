package controllers.webservice

import play.api.mvc.{Action, Controller}

/**
 * Created by Murat.
 */
object Application extends Controller{

  def index = Action{
    Ok("Hello from WEB SERVICE")
  }

}
