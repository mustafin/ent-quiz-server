package models.webservice

import java.sql.Timestamp
import _root_.util.Extensions._
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

case class Game(id: Option[Long], userOneId: Option[Long], userTwoId: Option[Long],
                createdAt: Option[Timestamp], userOneMove: Boolean = true, scoreOne: Int = 0, scoreTwo: Int = 0){
  def toGameData(user: GameUser) = {
    val userTwo = user.id match{
      case this.userOneId => GameUserDAO.find(this.userTwoId)
      case this.userTwoId => GameUserDAO.find(this.userOneId)
      case _ => None
    }

    GameData(this.id, Some(user), userTwo, this.scoreOne, this.scoreTwo)
  }

  def isReply(roundNum: Int, userId: Long): Boolean = {
    if(userOneId.isDefined || userTwoId.isDefined)
      if(roundNum % 2 == 0){
        userId == userOneId.get
      }else{
        userId == userTwoId.get
      }
    else false
  }


  def myMove(user: GameUser): Boolean ={
    if(user.id === userOneId){
      userOneMove
    }else if(user.id === userTwoId){
      !userOneMove
    }else false
  }

}

class GameTable(tag: Tag) extends Table[Game](tag, "GAME"){

  def id = column[Option[Long]]("ID", O.PrimaryKey, O.AutoInc)
  def userOneId = column[Option[Long]]("USER_ONE")
  def userTwoId = column[Option[Long]]("USER_TWO")
  def createdAt = column[Option[Timestamp]]("CREATED_AT")
  def userOneMove = column[Boolean]("USER_ONE_MOVE")
  def scoreOne = column[Int]("SCORE_ONE")
  def scoreTwo = column[Int]("SCORE_TWO")

  override def * = (id, userOneId, userTwoId, createdAt, userOneMove, scoreOne, scoreTwo) <> (Game.tupled, Game.unapply)

  def userOne = foreignKey("USER_ONE_FK", userOneId, TableQuery[GameUserTable])(_.id.get, onDelete=ForeignKeyAction.Cascade)
  def userTwo = foreignKey("USER_TWO_FK", userTwoId, TableQuery[GameUserTable])(_.id.get, onDelete=ForeignKeyAction.Cascade)

}

object GameObject{
//  def apply()
}

object GameDAO{

  val db = DatabaseConfigProvider.get[JdbcProfile](Play.current).db

  def tempClear = {
    db.run(ServiceTables.games.delete)
  }

  def newGame(user: GameUser): Future[Game] = {
    val gameRes = db.run(ServiceTables.games.
      filter(x => x.userTwoId.isEmpty && x.userOneId =!= user.id).result.headOption)
    gameRes.flatMap {
      case Some(g) =>
        db.run(ServiceTables.games.filter(_.id === g.id).map(_.userTwoId).update(user.id))
        Future.successful(g.copy(userTwoId = user.id))
      case None =>
        val game = Game(None, user.id, None, Some(new Timestamp(new java.util.Date().getTime)))
        db.run((ServiceTables.games returning ServiceTables.games.map(_.id)
          into ((user,id) => user.copy(id = id))) += game)
    }
  }

  def find(id: Option[Long]): Future[Option[Game]] ={
    db.run(ServiceTables.games.filter(_.id === id).result.headOption)
  }

  def toggleMove(game: Game): Unit ={
    val changeMove = ServiceTables.games.filter(_.id === game.id).map(_.userOneMove)
    db.run(changeMove.update(!game.userOneMove))
  }


  /**
   * Returns Categories, questions with answers
   *
   * @param round If round exist, then it is opponent move
   * @return Future<Seq<GameCategory>>
   */
  def moveData(game: Game, round: Option[Round], reply: Boolean): Future[Seq[GameCategory]] = {

    if(round.isDefined && round.get.empty)return Future.successful(Nil)

    val rand = SimpleFunction.nullary[Double]("rand")

//    val categ = round.flatMap(_.categoryId) match {
//      case Some(n) => Tables.categories.filter(_.id === n)
//      case None =>
//        val playedCats = ServiceTables.rounds.filter(_.gameId === gameId).map(_.categoryId)
//        Tables.categories.filter(row => !(row.id in playedCats)).sortBy(_ => rand).take(3)
//    }

    val categ = if(reply){
      Tables.categories.filter(_.id === round.get.categoryId)
    }else {
      val playedCats = ServiceTables.rounds.filter(_.gameId === game.id).map(_.categoryId)
      Tables.categories.filter(row => !(row.id in playedCats)).sortBy(_ => rand).take(3)
    }

    val categoriesFut = db.run(categ.result)

    val quesAndAnswersFut = categoriesFut.flatMap(
      listOfCat => {
        val results =
          if(listOfCat.length == 1)
          Seq(db.run(Tables.questions.filter(_.id inSet round.get.questions).withAnswers.result))
        else{
          for (category <- listOfCat) yield {
            val query = Tables.questions.filter(_.catId === category.id).sortBy(r => rand).take(3).withAnswers
            db.run(query.result)
          }
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
          val gameQuestions = items.filter(_._1.catId == x.id.get).groupBy(_._1).mapValues(_.map(_._2))
          .map{
            case (qes, ans) => GameQuestion(qes, ans.flatten, round.flatMap(_.userAnswers(game)(qes.id)))
          }.toVector
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
    implicit val gmeRound = Json.format[GameRound]

    val rds: Reads[Timestamp] = (__ \ "timestamp").read[Long].map{ long => new Timestamp(long) }
    val wrs: Writes[Timestamp] = (__ \ "timestamp").write[Long].contramap{ (a: Timestamp) => a.getTime }
    implicit val timestampFormat: Format[Timestamp] = Format(rds, wrs)
    implicit val gameFormat = Json.format[Game]
  }

}


