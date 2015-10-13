package models.webservice


import models.admin.{Tables, User, UserDAO}
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import slick.driver.JdbcProfile

import scala.slick.driver.MySQLDriver.simple._
import slick.lifted.{TableQuery, Tag}
import play.api.Play.current

import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax

/**
 * Created by Murat.
 */


case class GameUser(id: Option[Long], username: String, password: String, rating: Option[Int])



class GameUserTable(tag: Tag) extends Table[GameUser](tag, "GAME_USER"){

  def id = column[Option[Long]]("ID", O.PrimaryKey, O.AutoInc)
  def username = column[String]("USERNAME")
  def password = column[String]("PASSWORD")
  def rating = column[Option[Int]]("RATING")

  override def * = (id, username, password, rating) <> (GameUser.tupled, GameUser.unapply)

}

object GameUserDAO{

  val db = DatabaseConfigProvider.get[JdbcProfile](Play.current).db


  def register(user: GameUser) = db withSession{ implicit session =>
    val userToInsert = user.copy(password = UserDAO.encryptPassword(user.password))
    (ServiceTables.users += userToInsert).run
  }

  def findByName(username: String): Option[GameUser] = db withSession{ implicit  session =>
    ServiceTables.users.filter(_.username === username).firstOption
  }

  def find(id: Option[Long]) = db withSession {implicit session =>
    if(id.isDefined)
      ServiceTables.users.filter(_.id === id).firstOption
    else None
  }

  def checkCredentials(username: String, password: String): Boolean = db withSession {
    implicit session =>
      val encrypted = UserDAO.encryptPassword(password)
      ServiceTables.users.filter(x => x.username === username && x.password === encrypted).exists.run
  }

  implicit object GameUserFormat extends Format[GameUser] {
    def reads(json: JsValue): JsResult[GameUser] = (
      (JsPath \ "id").readNullable[Long] and
        (JsPath \ "username").read[String] and
        Reads.pure("") and
        (JsPath \ "rating").readNullable[Int]
      )(GameUser.apply _).reads(json)

    def writes(o: GameUser): JsValue = Json.obj(
      "id" -> o.id,
      "username" -> o.username,
      "password" -> "",
      "rating" -> o.rating
    )
  }

  def fromJsObj(obj: JsObject): Option[GameUser] = {
    obj.validate[GameUser] match {
      case JsSuccess(user, _) => Some(user)
      case err @ JsError(_) => None
    }
  }
  
}