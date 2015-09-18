package models.webservice

import java.sql.Timestamp
import models.admin._
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

case class Game(id: Option[Long], userOneId: Option[Long], userTwoId: Option[Long], createdAt: Option[Timestamp])

class GameTable(tag: Tag) extends Table[Game](tag, "GAME"){

  def id = column[Option[Long]]("ID", O.PrimaryKey, O.AutoInc)
  def userOneId = column[Option[Long]]("USER_ONE")
  def userTwoId = column[Option[Long]]("USER_TWO")
  def createdAt = column[Option[Timestamp]]("CREATED_AT")

  override def * = (id, userOneId, userTwoId, createdAt) <> (Game.tupled, Game.unapply)

  def userOne = foreignKey("USER_ONE_FK", userOneId, TableQuery[GameUserTable])(_.id.get, onDelete=ForeignKeyAction.Cascade)
  def userTwo = foreignKey("USER_TWO_FK", userTwoId, TableQuery[GameUserTable])(_.id.get, onDelete=ForeignKeyAction.Cascade)

}

object GameDAO{

  val db = DatabaseConfigProvider.get[JdbcProfile](Play.current).db

  def newGame(user: GameUser): Unit = {

    ServiceTables.games
    Game(None, user.id, None, None)
  }


  def prepareData(): JsObject = db withSession { implicit session =>
    val rand = SimpleFunction.nullary[Double]("random")

    val categories = Tables.categories.sortBy(x => rand).take(3).withQuestions.run.map{
      case (x, y) =>
        Json.toJson(x).as[JsObject] + ("questions" -> Json.toJson(y))
    }




    Json.obj("a" -> "f")
  }

}
