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
                createdAt: Option[Timestamp], userOneMove: Boolean = true, scoreOne: Int = 0, scoreTwo: Int = 0){
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

  def by(user: GameUser) = user.id === userOneId
  def opp(user: GameUser) = user.id === userTwoId

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
    db.run(Games.delete)
  }

  def newGame(user: GameUser): Future[Game] = {
    val gameRes = db.run(Games.
      filter(x => x.userTwoId.isEmpty && x.userOneId =!= user.id).result.headOption)
    gameRes.flatMap {
      case Some(g) =>
        db.run(Games.filter(_.id === g.id).map(_.userTwoId).update(user.id))
        Future.successful(g.copy(userTwoId = user.id))
      case None =>
        val game = Game(None, user.id, None, Some(new Timestamp(new java.util.Date().getTime)))
        db.run((Games returning Games.map(_.id)
          into ((user,id) => user.copy(id = id))) += game)
    }
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

  /**
   * Returns Categories, questions with answers
   *
   * @param round If round exist, then it is opponent move
   * @return Future<Seq<GameCategory>>
   */
  @Deprecated
  def moveData(game: Game, round: Option[Round], reply: Boolean): Future[Seq[GameCategory]] = {

    if(round.isDefined && round.get.empty)return Future.successful(Nil)

    val rand = SimpleFunction.nullary[Double]("rand")

    val categ = if(reply){
      Categories.filter(_.id === round.get.categoryId)
    }else {
      val playedCats = Rounds.filter(_.gameId === game.id).map(_.categoryId)
      Categories.filter(row => !(row.id in playedCats)).sortBy(_ => rand).take(3)
    }

    val categoriesFut = db.run(categ.result)

    val quesAndAnswersFut = categoriesFut.flatMap(
      listOfCat => {
        val results =
          if(listOfCat.length == 1)
          Seq(db.run(Questions.filter(_.id inSet round.get.questions).withAnswers.result))
        else{
          for (category <- listOfCat) yield {
            val query = Questions.filter(_.catId === category.id).sortBy(r => rand).take(3).withAnswers
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

  def oneCategoryData(round: Round): (Future[Seq[Category]], Future[Seq[(Question, Option[Answer])]]) = {
    val categ = Categories.filter(_.id === round.categoryId)
    db.run(categ.result) ->
    db.run(Questions.filter(_.id inSet round.questions).withAnswers.result)
  }

  def multipleCategoriesData(game: Game, num: Int): (Future[Seq[Category]], Future[Seq[(Question, Option[Answer])]]) = {
    val rand = SimpleFunction.nullary[Double]("rand")
    val playedCats = Rounds.filter(_.gameId === game.id).map(_.categoryId)
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


