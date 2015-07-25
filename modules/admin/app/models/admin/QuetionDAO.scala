package models.admin

import play.api.Play.current
import play.api.db.DB

import scala.slick.driver.MySQLDriver.simple._

/**
 * Created by Murat.
 */
object QuetionDAO {

  val db = Database.forDataSource(DB.getDataSource())
  lazy val questions = Tables.questions

  def list(catId: Int) = db.withSession { implicit session => questions.filter(_.catId === catId).list }

  def create(catId: Int) = ???

  def update(id: Int) = ???

  def delete(id: Int) = ???

  def deleteAnswer(id: Int) = ???

  def deleteImage(id: Int) = ???

}
