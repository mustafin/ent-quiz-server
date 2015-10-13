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

/**
 * Created by Murat.
 */
case class Round(id: Option[Long], gameId: Option[Long], categoryId: Option[Long],
                 quesOneId: Option[Long], quesTwoId: Option[Long], quesThreeId: Option[Long],
                 uoneAnsOneId: Option[Long], uoneAnsTwoId: Option[Long], uoneAnsThreeId: Option[Long],
                 utwoAnsOneId: Option[Long], utwoAnsTwoId: Option[Long], utwoAnsThreeId: Option[Long]){

  def finished = uoneAnsOneId.isDefined && uoneAnsTwoId.isDefined && uoneAnsThreeId.isDefined &&
                 utwoAnsOneId.isDefined && utwoAnsTwoId.isDefined && utwoAnsThreeId.isDefined

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

  def add(round: Round): Future[Unit] ={
    db.run(ServiceTables.rounds.insertOrUpdate(round)).map(_ => ())
  }

  def roundNum(gameId: Option[Long]): Future[Int] = {
    db.run(ServiceTables.rounds.filter(_.gameId === gameId).length.result)
  }

  def lastRound(gameId: Option[Long]): Future[Option[Round]] = {
    db.run(ServiceTables.rounds.filter(_.gameId === gameId).result.headOption)
  }


  object Implicits{
    implicit val roundFormat = Json.format[Round]
  }
}
