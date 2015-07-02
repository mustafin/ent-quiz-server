package controllers

import models.Category
import play.api.data.Forms._
import play.api.data._
import play.api.data.Form
import play.api.mvc.{Action, Controller}



/**
 * Created by Murat.
 */
object CategoryController extends Controller{

  val form = Form(
    mapping(
      "name" -> text
    )(Category.apply) (Category.unapply)
  )

  def list = Action{
    Ok(views.html.category.list(form))
  }

  def add = Action{
    Redirect(routes.CategoryController.list())
  }

}
