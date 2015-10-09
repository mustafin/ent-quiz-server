package models.admin

import play.api.Play
import play.api.Play.current
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.slick.driver.MySQLDriver.simple._

/**
 * Created by Murat.
 */
object QuetionDAO {

  val db = DatabaseConfigProvider.get[JdbcProfile](Play.current).db
  lazy val questions = Tables.questions

  def list(catId: Long) = db.withSession { implicit session => questions.filter(_.catId === catId).list }

  def create(catId: Long) = ???

  def update(id: Long) = ???

  def delete(id: Long) = ???

  def deleteAnswer(id: Long) = ???

  def deleteImage(id: Long) = ???

}
