package controllers

import models.{Category, CategoryTable}
import play.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick._
import play.api.mvc.Controller

import scala.slick.lifted.TableQuery

/**
 * Created by Murat.
 */
object CategoryController extends Controller{


  lazy val categories = TableQuery[CategoryTable]

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
    val category = form.bindFromRequest.get
    categories.insert(category)
    Redirect(routes.CategoryController.list())
  }

  def edit(id: Int) = DBAction{ implicit rs =>
    val category = categories.filter(_.id === id).firstOption
    if(category.isDefined)
      Ok(views.html.category.edit(category.get, form.fill(category.get)))
    else NotFound("Not FOund")
  }

  def updateCategory(id: Int) = DBAction{ implicit rs =>
    val category = form.bindFromRequest.get
    val categoryToUpdate: Category = category.copy(Some(id))
    categories.filter(_.id === id).update(categoryToUpdate)
    Redirect(routes.CategoryController.list())
  }

  def delete(id: Int) = DBAction{ implicit rs =>
    categories.filter(_.id === id).delete
    Redirect(routes.CategoryController.list())
  }

}
