package controllers

import play.api.mvc.{Action, Controller}

/**
 * Created by Murat.
 */
object CategoryController extends Controller{

  def list = Action{
    Ok(views.html.category.list)
  }



}
