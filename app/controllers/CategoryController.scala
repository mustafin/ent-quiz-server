package controllers

import models.{CategoryTable, Tables, Category}
import play.Logger
import play.api.data.Forms._
import play.api.data._
import play.api.data.Form
import play.api.mvc.{Action, Controller}
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._

/**
 * Created by Murat.
 */
object CategoryController extends Controller{


  val categories = Tables.categories

  val form = Form(
    mapping(
      "id" -> ignored[Option[Int]](None),
      "name" -> text
    )(Category.apply) (Category.unapply)
  )

  def list = DBAction{ implicit rs =>
    Logger.debug(categories.ddl.createStatements.mkString(", "))
    Ok(views.html.category.list(form, categories.list))
  }

  def add = DBAction{ implicit request =>
    val person = form.bindFromRequest.get
    categories.insert(person)
    Redirect(routes.CategoryController.list())
  }

  def edit(id: Option[Int]) = Action{
    Redirect(routes.CategoryController.list())
  }

  def delete(id: Option[Int]) = Action{
//    DB.delete(DB.fetchById(id))
    Redirect(routes.CategoryController.list())
  }

}
