package models.webservice

import java.sql.Timestamp
import models.admin._
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import slick.driver.JdbcProfile

import slick.driver.MySQLDriver.api._
import slick.lifted.{TableQuery, Tag}

import play.api.libs.json.Reads._

import scala.concurrent.Future

// Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax
import play.api.libs.concurrent.Execution.Implicits._

/**
 * Created by Murat.
 */

case class Game(id: Option[Long], userOneId: Option[Long], userTwoId: Option[Long], createdAt: Option[Timestamp], scoreOne: Int = 0, scoreTwo: Int = 0){
  def toGameData(user: GameUser) = {
    val userTwo = user.id match{
      case this.userOneId => GameUserDAO.find(this.userTwoId)
      case this.userTwoId => GameUserDAO.find(this.userOneId)
      case _ => None
    }

    GameData(this.id, Some(user), userTwo, this.scoreOne, this.scoreTwo)
  }
}

class GameTable(tag: Tag) extends Table[Game](tag, "GAME"){

  def id = column[Option[Long]]("ID", O.PrimaryKey, O.AutoInc)
  def userOneId = column[Option[Long]]("USER_ONE")
  def userTwoId = column[Option[Long]]("USER_TWO")
  def createdAt = column[Option[Timestamp]]("CREATED_AT")
  def scoreOne = column[Int]("SCORE_ONE")
  def scoreTwo = column[Int]("SCORE_TWO")

  override def * = (id, userOneId, userTwoId, createdAt, scoreOne, scoreTwo) <> (Game.tupled, Game.unapply)

  def userOne = foreignKey("USER_ONE_FK", userOneId, TableQuery[GameUserTable])(_.id.get, onDelete=ForeignKeyAction.Cascade)
  def userTwo = foreignKey("USER_TWO_FK", userTwoId, TableQuery[GameUserTable])(_.id.get, onDelete=ForeignKeyAction.Cascade)

}




object GameObject{
//  def apply()
}

object GameDAO{

  val db = DatabaseConfigProvider.get[JdbcProfile](Play.current).db

  def newGame(user: GameUser): Future[Game] = {
    val gameRes = db.run(ServiceTables.games.
      filter(x => x.userTwoId.isEmpty && x.userOneId =!= user.id).result.headOption)
    gameRes.flatMap {
      x => {
        val game = x match {
          case Some(g) => g.copy(userTwoId = user.id)
          case None => Game(None, user.id, None, Some(new Timestamp(new java.util.Date().getTime)))
        }
        db.run((ServiceTables.games returning ServiceTables.games.map(_.id)
            into ((user,id) => user.copy(id = id))).insertOrUpdate(game)).map(_.get)
      }
    }
  }

  def find(id: Option[Long]): Future[Option[Game]] ={
    db.run(ServiceTables.games.filter(_.id === id).result.headOption)
  }

  /**
   * Returns Categories, questions with answers
   *
   * @param catId If catId exist, then it is opponent move
   * @return Future<Seq<GameCategory>>
   */
  def moveData(catId: Option[Long] = None): Future[Seq[GameCategory]] = {

    val rand = SimpleFunction.nullary[Double]("rand")

    val categ = catId match {
      case Some(n) => Tables.categories.filter(_.id === n)
      case None => Tables.categories.sortBy(x => rand).take(3)
    }

    val categoriesFut = db.run(categ.result)

    val quesAndAnswersFut = categoriesFut.flatMap(
      listOfCat => {
        val results = for (category <- listOfCat) yield {
          val query = Tables.questions.filter(_.catId === category.id).sortBy(r => rand).take(3).withAnswers
          db.run(query.result)
        }
        Future.sequence(results)
      }
    ).map(_.flatten)

    for{
      categories <- categoriesFut
      items <- quesAndAnswersFut
    }yield{
      categories.map(
        x => {
          //converting List[Tuple3] to List[k -> (k -> v)]
          val gameQuestions = items.filter(_._1.catId == x.id.get)
                                .groupBy(_._1)
                                .mapValues(_.map(_._2))
          .map{
            case (qes, ans) => GameQuestion(qes, ans.flatten, None) //TODO add answered id HERE
          }.toSeq
          GameCategory(x, gameQuestions)
        }
      )
    }

  }

  object Implicits{

    implicit val answerFormat = Json.format[Answer]
    implicit val quesFormat = Json.format[Question]
    implicit val gameQuesFormat = Json.format[GameQuestion]
    implicit val catFormat = Json.format[Category]
    implicit val gameUserFormat = GameUserDAO.GameUserFormat
    implicit val gameCatFormat = Json.format[GameCategory]
    implicit val gameDataFormat = Json.format[GameData]

    val rds: Reads[Timestamp] = (__ \ "timestamp").read[Long].map{ long => new Timestamp(long) }
    val wrs: Writes[Timestamp] = (__ \ "timestamp").write[Long].contramap{ (a: Timestamp) => a.getTime }
    implicit val timestampFormat: Format[Timestamp] = Format(rds, wrs)
    implicit val gameFormat = Json.format[Game]
  }

}


