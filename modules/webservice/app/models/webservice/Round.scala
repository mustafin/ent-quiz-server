package models.webservice

import models.admin.{AnswerTable, CategoryTable, QuestionTable}
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import slick.lifted.{TableQuery, Tag}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import util.Extensions._

import scala.util.Success

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
    utwoAnsOneId.isEmpty && utwoAnsTwoId.isEmpty && utwoAnsThreeId.isDefined

  def playerAnswers = Map(quesOneId -> uoneAnsOneId, quesTwoId -> uoneAnsTwoId, quesThreeId -> uoneAnsThreeId).withDefaultValue(None)

  def opponentAnswers = Map(quesOneId -> utwoAnsOneId, quesTwoId -> utwoAnsTwoId, quesThreeId -> utwoAnsThreeId).withDefaultValue(None)

  def userAnswers(game: Game) = if (game.userOneMove) opponentAnswers else playerAnswers

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

  def add(round: Round): Unit ={
    db.run(ServiceTables.rounds.insertOrUpdate(round))
  }

  def roundNum(gameId: Option[Long]): Future[Int] = {
    db.run(ServiceTables.rounds.filter(_.gameId === gameId).length.result)
  }

  def lastRound(gameId: Option[Long]): Future[Option[Round]] = {
    db.run(ServiceTables.rounds.filter(_.gameId === gameId).sortBy(_.id.desc).result.headOption)
  }

  def newRound(gameId: Option[Long]): Future[Round] ={
    val round = Round(None, gameId, None, None, None, None, None, None, None, None, None, None)
    db.run(ServiceTables.rounds returning ServiceTables.rounds.map(_.id)
      into ((user,id) => user.copy(id = id)) += round)
  }

  def submitRound(gr: GameRound, game: Game, userId: Option[Long]): Unit ={

      val query = ServiceTables.rounds.filter(g => g.gameId === gr.gameId && g.id === gr.roundId)
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

      } else Future.successful(-1)
      d.andThen{
        case Success(e) => //TODO: SIMPLIFY THIS
          if(e != -1)
            db.run(ServiceTables.rounds.filter(_.id === gr.roundId).result.headOption).map{
              rOp => rOp.foreach{
                r => if(!r.finished) GameDAO.toggleMove(game)
              }
            }
      }


  }

  object Implicits{
    implicit val roundFormat = Json.format[Round]
  }
}
