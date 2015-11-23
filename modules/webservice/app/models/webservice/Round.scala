package models.webservice

import models.admin._
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import slick.lifted.{TableQuery, Tag}
import util.Extensions._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
 * Created by Murat.
 */
case class Round(id: Option[Long], gameId: Option[Long], categoryId: Option[Long],
                 quesOneId: Option[Long], quesTwoId: Option[Long], quesThreeId: Option[Long],
                 uoneAnsOneId: Option[Long], uoneAnsTwoId: Option[Long], uoneAnsThreeId: Option[Long],
                 utwoAnsOneId: Option[Long], utwoAnsTwoId: Option[Long], utwoAnsThreeId: Option[Long]){

  def finished = uoneAnsOneId.isDefined && uoneAnsTwoId.isDefined && uoneAnsThreeId.isDefined &&
                 utwoAnsOneId.isDefined && utwoAnsTwoId.isDefined && utwoAnsThreeId.isDefined

  def empty = uoneAnsOneId.isEmpty && uoneAnsTwoId.isEmpty && uoneAnsThreeId.isEmpty &&
    utwoAnsOneId.isEmpty && utwoAnsTwoId.isEmpty && utwoAnsThreeId.isEmpty

  def leftAnswers = playerAnswersMap.values.flatten

  def rightAnswers = opponentAnswersMap.values.flatten

  def playerAnswersMap = Map(quesOneId -> uoneAnsOneId, quesTwoId -> uoneAnsTwoId, quesThreeId -> uoneAnsThreeId).withDefaultValue(None)

  def opponentAnswersMap = Map(quesOneId -> utwoAnsOneId, quesTwoId -> utwoAnsTwoId, quesThreeId -> utwoAnsThreeId).withDefaultValue(None)

  def userAnswers(game: Game) = if (game.userOneMove) opponentAnswersMap else playerAnswersMap

  def questions = Set(quesOneId, quesTwoId, quesThreeId).flatten

  def replyMove(count: Int):Boolean = {
    if(count % 2 == 0) finished
    else !finished
  }

}

class RoundTable(tag: Tag) extends Table[Round](tag, "ROUND"){

  def id = column[Option[Long]]("ID", O.PrimaryKey, O.AutoInc)
  def gameId = column[Option[Long]]("GAME_ID")
  def categoryId = column[Option[Long]]("CATEGORY_ID")
  def quesOneId = column[Option[Long]]("QUES_ONE")
  def quesTwoId = column[Option[Long]]("QUES_TWO")
  def quesThreeId = column[Option[Long]]("QUES_THREE")
  def uoneAnsOneId = column[Option[Long]]("UONE_ANSONE")
  def uoneAnsTwoId = column[Option[Long]]("UONE_ANSTWO")
  def uoneAnsThreeId = column[Option[Long]]("UONE_ANSTHREE")
  def utwoAnsOneId = column[Option[Long]]("UTWO_ANSONE")
  def utwoAnsTwoId = column[Option[Long]]("UTWO_ANSTWO")
  def utwoAnsThreeId = column[Option[Long]]("UTWO_ANSTHREE")

  override def * = (id, gameId, categoryId,
    quesOneId, quesTwoId, quesThreeId,
    uoneAnsOneId, uoneAnsTwoId, uoneAnsThreeId,
    utwoAnsOneId, utwoAnsTwoId, utwoAnsThreeId) <> (Round.tupled, Round.unapply)

  def game = foreignKey("GAME_ID_FK", gameId, TableQuery[GameTable])(_.id)
  def category = foreignKey("CATEGORY_ID_FK", categoryId, TableQuery[CategoryTable])(_.id)
  def quesOne = foreignKey("QUES_ONE_FK", quesOneId, TableQuery[QuestionTable])(_.id)
  def quesTwo = foreignKey("QUES_TWO_FK", quesTwoId, TableQuery[QuestionTable])(_.id)
  def quesThree = foreignKey("QUES_THREE_FK", quesThreeId, TableQuery[QuestionTable])(_.id)
  def uoneAnsOne = foreignKey("UONE_ANSONE_FK", uoneAnsOneId, TableQuery[AnswerTable])(_.id)
  def uoneAnsTwo = foreignKey("UONE_ANSTWO_FK", uoneAnsTwoId, TableQuery[AnswerTable])(_.id)
  def uoneAnsThree = foreignKey("UONE_ANSTHREE_FK", uoneAnsThreeId, TableQuery[AnswerTable])(_.id)
  def utwoAnsOne = foreignKey("UTWO_ANSONE_FK", utwoAnsOneId, TableQuery[AnswerTable])(_.id)
  def utwoAnsTwo = foreignKey("UTWO_ANSTWO_FK", utwoAnsTwoId, TableQuery[AnswerTable])(_.id)
  def utwoAnsThree = foreignKey("UTWO_ANSTHREE_FK", utwoAnsThreeId, TableQuery[AnswerTable])(_.id)

}

object RoundDAO{
  val db = DatabaseConfigProvider.get[JdbcProfile](Play.current).db

  def find(id: Option[Long]): Future[Option[Round]] ={
    db.run(Rounds.filter(_.id === id).result.headOption)
  }

  def add(round: Round): Unit ={
    db.run(Rounds.insertOrUpdate(round))
  }

  def roundNum(gameId: Option[Long]): Future[Int] = {
    db.run(Rounds.filter(_.gameId === gameId).length.result)
  }

  def lastRound(gameId: Option[Long]): Future[Option[Round]] = {
    db.run(Rounds.filter(_.gameId === gameId).sortBy(_.id.desc).result.headOption)
  }

  def newRound(gameId: Option[Long]): Future[Round] ={
    val round = Round(None, gameId, None, None, None, None, None, None, None, None, None, None)
    db.run(Rounds returning Rounds.map(_.id)
      into ((user,id) => user.copy(id = id)) += round)
  }

  @Deprecated
  def submitRound(gr: GameRound, game: Game, userId: Option[Long]): Future[_] ={

    val query = Rounds.filter(g => g.gameId === gr.gameId && g.id === gr.roundId)
    val d = if (userId === game.userOneId) {
      val updateQ = query.map(r => (r.categoryId, r.quesOneId, r.quesTwoId, r.quesThreeId,
        r.uoneAnsOneId, r.uoneAnsTwoId, r.uoneAnsThreeId))
        .update((gr.catId, gr.q1Id, gr.q2Id, gr.q3Id, gr.a1Id, gr.a2Id, gr.a3Id))
      db.run(updateQ)
    } else if (userId === game.userTwoId) {
      val update = query.map(r => (r.categoryId, r.quesOneId, r.quesTwoId, r.quesThreeId,
        r.utwoAnsOneId, r.utwoAnsTwoId, r.utwoAnsThreeId))
        .update((gr.catId, gr.q1Id, gr.q2Id, gr.q3Id, gr.a1Id, gr.a2Id, gr.a3Id))
      db.run(update)
    } else throw new Exception("game and user does not match")
    d.andThen{
      case Success(e) =>
        db.run(Rounds.filter(_.id === gr.roundId).result.headOption).map{
          rOp => rOp.foreach{
            r =>
              if(!r.finished) GameDAO.toggleMove(game)
          }
        }
      case Failure(e) => Future.failed(e)
    }
  }

  def countScores(answers: Set[Long]): Future[Int] ={
    val query = Answers.filter(ans => (ans.id inSet answers) && ans.isTrue).length
    db.run(query.result)
  }

  def countAndSaveScores(answers: Set[Long], game: Game, left: Boolean): Future[Int] = {
    countScores(answers).flatMap{
      count => {
        val q = Games.filter(_.id === game.id)
        val update = if(left){
          q.map(_.scoreOne).update(game.scoreOne + count)
        }else{
          q.map(_.scoreTwo).update(game.scoreTwo + count)
        }
        db.run(update)
      }
    }
  }

  object Implicits{
    implicit val roundFormat = Json.format[Round]
  }
}
