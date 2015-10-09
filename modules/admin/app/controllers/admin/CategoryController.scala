package controllers.admin

import javax.inject.Inject

import models.admin.{Category, CategoryTable}
import play.api.Play
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Controller
import slick.driver.JdbcProfile

import scala.slick.driver.MySQLDriver.simple._

/**
 * Created by Murat.
 */
class CategoryController  @Inject() (val messagesApi: MessagesApi) extends Controller with Secured with I18nSupport{


  lazy val categories = TableQuery[CategoryTable]
  val db = DatabaseConfigProvider.get[JdbcProfile](Play.current).db

  def getCategories = db.withSession { implicit session => categories.list }

  val form = Form(
    mapping(
      "id" -> ignored[Option[Long]](None),
      "name" -> nonEmptyText
    )(Category.apply) (Category.unapply)
  )

  def list = withAuth{ username => implicit rs =>
    Ok(views.html.admin.category.list(form, getCategories))
  }

  def add = withAuth{ username => implicit rs =>
    form.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.admin.category.list(formWithErrors, getCategories))
      },
      category => {
        db.withSession { implicit session => categories.insert(category)}
        Redirect(routes.CategoryController.list())
      }
    )
  }

  def edit(id: Long) = withAuth{ username => implicit rs =>
    val category = db.withSession { implicit session => categories.filter(_.id === id).firstOption }
    if(category.isDefined)
      Ok(views.html.admin.category.edit(category.get, form.fill(category.get)))
    else NotFound("Not FOund")
  }

  def updateCategory(id: Long) = withAuth{ username => implicit rs =>
    val category = form.bindFromRequest.get
    val categoryToUpdate: Category = category.copy(Some(id))
    db.withSession { implicit session => categories.filter(_.id === id).update(categoryToUpdate)}

    Redirect(routes.CategoryController.list())
  }

  def delete(id: Long) = withAuth{ username => implicit rs =>
    db.withSession { implicit session => categories.filter(_.id === id).delete}
    Redirect(routes.CategoryController.list())
  }

}
