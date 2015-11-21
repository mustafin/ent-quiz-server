package models.webservice


import models.admin.{User, UserDAO}
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import slick.driver.JdbcProfile

import scala.concurrent.Future

//import scala.slick.driver.MySQLDriver.simple._
import slick.driver.MySQLDriver.api._
import slick.lifted.{TableQuery, Tag}
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax

/**
 * Created by Murat.
 */
case class GameUserDevice(userId: Long, deviceId: String, deviceOS: String)

class GameUserDevicesTable(tag: Tag) extends Table[GameUserDevice](tag, "GAME_USER_DEVICES"){

  def userId = column[Long]("USER_ID")
  def deviceId = column[String]("DEVICE_ID")
  def deviceOS = column[String]("DEVICE_OS")

  def ===(gameDevice: GameUserDevice) = userId === gameDevice.userId &&
                                      deviceId === gameDevice.deviceId && 
                                      deviceOS === gameDevice.deviceOS
  
  override def * = (userId, deviceId, deviceOS) <> (GameUserDevice.tupled, GameUserDevice.unapply)

}

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
  lazy val users = TableQuery[GameUserTable]
  lazy val devices = TableQuery[GameUserDevicesTable]

  def register(user: GameUser) = {
    val userToInsert = user.copy(password = UserDAO.encryptPassword(user.password))
    db.run(users += userToInsert)
  }

  def findByName(username: String): Future[Option[GameUser]] = {
    db.run(users.filter(_.username === username).result.headOption)
  }

  def find(id: Option[Long]) = {
    db.run(users.filter(_.id === id).result.headOption)
  }

  def checkCredentials(username: String, password: String): Future[Boolean] = {
    val encrypted = UserDAO.encryptPassword(password)
    db.run(users.filter(x => x.username === username && x.password === encrypted).exists.result)
  }

  def registerDevice(userDevice: GameUserDevice) = {
    val q = devices.filter(x => x === userDevice).exists.result.flatMap{
      exist =>
        if(!exist){
          devices += userDevice
        } else {
          DBIO.successful(None)
        }
    }.transactionally
    db.run(q)
  }

  def userDevices(userId: Long): Future[Seq[GameUserDevice]] = {
    db.run(devices.filter(_.userId === userId).result)
  }

  def userDevicesIds(userId: Long): Future[Seq[String]] = {
    db.run(devices.filter(_.userId === userId).map(_.deviceId).result)
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