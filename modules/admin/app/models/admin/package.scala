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

  object Tables{
    lazy val categories = TableQuery[CategoryTable]
    lazy val users = TableQuery[UserTable]
    lazy val questions = TableQuery[QuestionTable]
    lazy val answers = TableQuery[AnswerTable]

    def questionAnswers(qId: Int)(implicit db: Database) =
      Await.result(db.run(answers.filter(_.quesId === qId).result), 1.minute).toList

  }

  implicit val catFormat = Json.format[Category]
  implicit val quesFormat = Json.format[Question]

  case class User(id: Option[Int], username: String, password: String)

  class UserTable(tag: Tag) extends Table[User](tag, "USER"){
    def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)
    def username = column[String]("USERNAME")
    def password = column[String]("PASSWORD")

    override def * = (id, username, password) <> (User.tupled, User.unapply)
  }

  case class Category(id: Option[Int], name: String)

  class CategoryTable(tag: Tag) extends Table[Category](tag, "CATEGORY"){
    def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")

    override def * = (id, name) <> (Category.tupled, Category.unapply)
  }

  implicit class CategoryExtensions[C[_]](q: Query[CategoryTable, Category, C]) {
    // specify mapping of relationship to address
    def withQuestions = q.joinLeft(Tables.questions).on(_.id === _.catId)
  }


  case class Question(id: Option[Int], title: String, catId: Int, img: String){
    implicit val qFormat = Json.format[Question]
  }


  class QuestionTable(tag: Tag) extends Table[Question](tag, "QUESTION"){
    implicit val writes = Json.writes[Question]

    def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)
    def title = column[String]("TITLE")
    def catId = column[Int]("CATEGORY_ID")
    def img = column[String]("IMG")
    def category = foreignKey("CATEGORY_FK", catId, Tables.categories)(_.id.get, onDelete=ForeignKeyAction.Cascade)
    override def * = (id, title, catId, img) <> (Question.tupled, Question.unapply)

  }

  case class Answer(id: Option[Int], title: String, isTrue: Boolean, quesId: Int, img: String)

  class AnswerTable(tag: Tag) extends Table[Answer](tag, "ANSWER"){

    def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)
    def title = column[String]("TITLE")
    def isTrue = column[Boolean]("IS_TRUE")
    def quesId = column[Int]("QUESTION_ID")
    def img = column[String]("IMG")
    def question = foreignKey("QUESTION_FK", quesId, Tables.questions)(_.id.get)

    override def * = (id, title, isTrue, quesId, img) <> (Answer.tupled, Answer.unapply)

  }


}
