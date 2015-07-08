package controllers

import models.{CategoryTable, Category}
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.DB
import scala.slick.driver.MySQLDriver.simple._
import play.api.db.slick.{Database => _, _}
import play.api.mvc.Controller
import play.api.Play.current


import scala.slick.lifted.TableQuery

/**
 * Created by Murat.
 */
object CategoryController extends Controller with Secured{


  lazy val categories = TableQuery[CategoryTable]
  lazy val db = Database.forDataSource(DB.getDataSource())

  def getCategories = db.withSession { implicit session => categories.list }

  val form = Form(
    mapping(
      "id" -> ignored[Option[Int]](None),
      "name" -> text
    )(Category.apply) (Category.unapply)
  )

  def list = withAuth{ username => implicit rs =>
    Ok(views.html.category.list(form, getCategories))
  }

  def add = withAuth{ username => implicit rs =>
    form.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.category.list(formWithErrors, getCategories))
      },
      category => {
        db.withSession { implicit session => categories.insert(category)}
        Redirect(routes.CategoryController.list())
      }
    )
  }

  def edit(id: Int) = DBAction{ implicit rs =>
    val category = db.withSession { implicit session => categories.filter(_.id === id).firstOption }
    if(category.isDefined)
      Ok(views.html.category.edit(category.get, form.fill(category.get)))
    else NotFound("Not FOund")
  }

  def updateCategory(id: Int) = DBAction{ implicit rs =>
    val category = form.bindFromRequest.get
    val categoryToUpdate: Category = category.copy(Some(id))
    db.withSession { implicit session => categories.filter(_.id === id).update(categoryToUpdate)}

    Redirect(routes.CategoryController.list())
  }

  def delete(id: Int) = DBAction{ implicit rs =>
    db.withSession { implicit session => categories.filter(_.id === id).delete}
    Redirect(routes.CategoryController.list())
  }

}
