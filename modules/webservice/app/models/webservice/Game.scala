package models.webservice

import java.sql.Timestamp
import _root_.util.Extensions._
import gameservice.{FirstMove, ReplyMove, Move}
import models.admin._
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import slick.driver.JdbcProfile

import slick.driver.MySQLDriver.api._
import slick.lifted.{TableQuery, Tag}

import play.api.libs.json.Reads._

import scala.concurrent.Future
import scala.util.Success


// Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax
import play.api.libs.concurrent.Execution.Implicits._

/**
 * Created by Murat.
 */

case class Game(id: Option[Long], userOneId: Option[Long], userTwoId: Option[Long],
                createdAt: Option[Timestamp], userOneMove: Boolean = true,
                finished: Boolean = false,
                scoreOne: Int = 0, scoreTwo: Int = 0){
  def toGameData(user: GameUser): Future[GameData] = {
    val userTwo = user.id match{
      case this.userOneId => GameUserDAO.find(this.userTwoId)
      case this.userTwoId => GameUserDAO.find(this.userOneId)
      case _ => Future.successful(None)
    }
    userTwo.map(uTwo =>GameData(this.id, Some(user), uTwo, this.scoreOne, this.scoreTwo))
  }

  def myMove(user: GameUser): Boolean ={
    if(user.id === userOneId){
      userOneMove
    }else if(user.id === userTwoId){
      !userOneMove
    }else false
  }

  def by(userId: Option[Long]): Boolean = userId === userOneId
  def opp(userId: Option[Long]): Boolean = userId === userTwoId
  def by(user: GameUser): Boolean = this.by(user.id)
  def opp(user: GameUser): Boolean = this.opp(user.id)

}

class GameTable(tag: Tag) extends Table[Game](tag, "GAME"){

  def id = column[Option[Long]]("ID", O.PrimaryKey, O.AutoInc)
  def userOneId = column[Option[Long]]("USER_ONE")
  def userTwoId = column[Option[Long]]("USER_TWO")
  def createdAt = column[Option[Timestamp]]("CREATED_AT")
  def userOneMove = column[Boolean]("USER_ONE_MOVE")
  def finished = column[Boolean]("FINISHED")
  def scoreOne = column[Int]("SCORE_ONE")
  def scoreTwo = column[Int]("SCORE_TWO")

  override def * = (id, userOneId, userTwoId, createdAt, userOneMove, finished, scoreOne, scoreTwo) <> (Game.tupled, Game.unapply)

  def userOne = foreignKey("USER_ONE_FK", userOneId, TableQuery[GameUserTable])(_.id.get, onDelete=ForeignKeyAction.Cascade)
  def userTwo = foreignKey("USER_TWO_FK", userTwoId, TableQuery[GameUserTable])(_.id.get, onDelete=ForeignKeyAction.Cascade)

}

object GameObject{
//  def apply()

}

object GameDAO{

  import _root_.util.GameConsts._

  val db = DatabaseConfigProvider.get[JdbcProfile](Play.current).db

  def tempClear = {
    db.run(Games.delete)
  }

  def gameRounds(id: Option[Long]): Future[Seq[Round]] = {
    db.run(Rounds.filter(_.gameId === id).result)
  }

  def newGameOrJoin(user: GameUser): Future[Game] = {
    val gameRes = db.run(Games.
      filter(x => x.userTwoId.isEmpty && x.userOneId =!= user.id).result.headOption)
    gameRes.flatMap {
      case Some(g) =>
        db.run(Games.filter(_.id === g.id).map(_.userTwoId).update(user.id).transactionally)
        Future.successful(g.copy(userTwoId = user.id))
      case None =>
        newGame(user)
    }
  }

  def newGame(user: GameUser, opponent: Option[Long] = None): Future[Game] = {
    val game = Game(None, user.id, opponent, Some(new Timestamp(new java.util.Date().getTime)))
    db.run((Games returning Games.map(_.id)
      into ((user,id) => user.copy(id = id))) += game)
  }

  def finishGame(gameId: Option[Long]): Unit ={
    db.run(Games.filter(_.id === gameId).map(_.finished).update(true))
  }

  /**
   * Before using method check if last round is finished
   */
  def isLastRound(gameId: Option[Long]): Future[Boolean] = {
    RoundDAO.roundNum(gameId).map(_ >= CATEG_NUM)
  }

  //TODO remove
  def devices(): Future[Seq[GameUserDevice]] = {
    db.run(Devices.result)
  }

  def find(id: Option[Long]): Future[Option[Game]] ={
    db.run(Games.filter(_.id === id).result.headOption)
  }

  def toggleMove(game: Game): Unit ={
    val changeMove = Games.filter(_.id === game.id).map(_.userOneMove)
    db.run(changeMove.update(!game.userOneMove))
  }


  def oneCategoryData(round: Round): (Future[Seq[Category]], Future[Seq[(Question, Option[Answer])]]) = {
    val categ = Categories.filter(_.id === round.categoryId).result
    val ques = Questions.filter(_.id inSet round.questions).withAnswers.result

    db.run(categ) -> db.run(ques)
  }

  def multipleCategoriesData(game: Game, num: Int): (Future[Seq[Category]], Future[Seq[(Question, Option[Answer])]]) = {

    val rand = SimpleFunction.nullary[Double]("rand")
    val playedCats = Rounds.filter(r => r.gameId === game.id && r.categoryId.isDefined).map(_.categoryId)
    val categ = Categories.filter(row => !(row.id in playedCats)).sortBy(_ => rand).take(num)
    val q = db.run(categ.result)
    q -> q.flatMap{
      listOfCat =>
        Future.sequence(for (category <- listOfCat) yield {
          val query = Questions.filter(_.catId === category.id).sortBy(r => rand).take(num).withAnswers
          db.run(query.result)
        }).map(_.flatten)
    }

    /* -- TODO: CHANGE TO THIS OPTIMIZED QUERY
      SELECT CQ.CID, CQ.NAME, CQ.QID, CQ.TITLE, CQ.CATEGORY_ID, CQ.IMG, CQ.AID, CQ.IS_TRUE, CQ.ATITLE, CQ.QUESTION_ID, CQ.AIMG
  FROM (SELECT C.ID AS CID, C.NAME, Q.ID AS QID, Q.TITLE, Q.CATEGORY_ID, Q.IMG, A.ID AS AID, A.IS_TRUE, A.TITLE AS ATITLE, A.QUESTION_ID, A.IMG AS AIMG,
  	(@RN := IF(@C = C.ID, @RN + 1, IF(@C := C.ID, 1, 1))) AS RN
    FROM (SELECT C.ID, C.NAME
      FROM CATEGORY C
      ORDER BY RAND()
      LIMIT 3
      ) C JOIN
    QUESTION Q
    ON C.ID = Q.CATEGORY_ID
  	JOIN ANSWER A ON Q.ID = A.QUESTION_ID
  	CROSS JOIN
    (SELECT @C := 0, @RN := 0) PARAMS
  	ORDER BY C.ID, RAND()
  ) CQ
WHERE RN <= 3
     */
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


