package models


import play.api.libs.json.Json

import slick.driver.MySQLDriver.api._
import slick.jdbc.JdbcBackend
import slick.lifted.{TableQuery, Tag}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Created by Murat.
 */
package object admin {

  lazy val Categories = TableQuery[CategoryTable]
  lazy val Users = TableQuery[UserTable]
  lazy val Questions = TableQuery[QuestionTable]
  lazy val Answers = TableQuery[AnswerTable]

  def questionAnswers(qId: Long)(implicit db: Database) =
    Await.result(db.run(Answers.filter(_.quesId === qId).result), 1.minute).toList


  implicit val catFormat = Json.format[Category]
  implicit val quesFormat = Json.format[Question]

  case class User(id: Option[Long], username: String, password: String)

  class UserTable(tag: Tag) extends Table[User](tag, "USER"){
    def id = column[Option[Long]]("ID", O.PrimaryKey, O.AutoInc)
    def username = column[String]("USERNAME")
    def password = column[String]("PASSWORD")

    override def * = (id, username, password) <> (User.tupled, User.unapply)
  }

  case class Category(id: Option[Long], name: String)

  class CategoryTable(tag: Tag) extends Table[Category](tag, "CATEGORY"){
    def id = column[Option[Long]]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")

    override def * = (id, name) <> (Category.tupled, Category.unapply)
  }

  implicit class CategoryExtensions[C[_]](q: Query[CategoryTable, Category, C]) {

    def with3Questions = q.joinLeft(Questions).on(_.id === _.catId)
  }

  implicit class QuestionExtensions[C[_]](q: Query[QuestionTable, Question, C]) {
    def withAnswers = q.joinLeft(Answers).on(_.id === _.quesId)
  }


  case class Question(id: Option[Long], title: String, catId: Long, img: String){
    implicit val qFormat = Json.format[Question]
  }


  class QuestionTable(tag: Tag) extends Table[Question](tag, "QUESTION"){
    implicit val writes = Json.writes[Question]

    def id = column[Option[Long]]("ID", O.PrimaryKey, O.AutoInc)
    def title = column[String]("TITLE")
    def catId = column[Long]("CATEGORY_ID")
    def img = column[String]("IMG")
    def category = foreignKey("CATEGORY_FK", catId, Categories)(_.id.get, onDelete=ForeignKeyAction.Cascade)
    override def * = (id, title, catId, img) <> (Question.tupled, Question.unapply)

  }

  case class Answer(id: Option[Long], title: String, isTrue: Boolean, quesId: Long, img: String)

  class AnswerTable(tag: Tag) extends Table[Answer](tag, "ANSWER"){

    def id = column[Option[Long]]("ID", O.PrimaryKey, O.AutoInc)
    def title = column[String]("TITLE")
    def isTrue = column[Boolean]("IS_TRUE")
    def quesId = column[Long]("QUESTION_ID")
    def img = column[String]("IMG")
    def question = foreignKey("QUESTION_FK", quesId, Questions)(_.id.get)

    override def * = (id, title, isTrue, quesId, img) <> (Answer.tupled, Answer.unapply)

  }


}
