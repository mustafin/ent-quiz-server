
import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.JdbcBackend

import scala.slick.lifted.{TableQuery, Tag}

/**
 * Created by Murat.
 */
package object models {

  object Tables{
    lazy val categories = TableQuery[CategoryTable]
    lazy val users = TableQuery[UserTable]
    lazy val questions = TableQuery[QuestionTable]
    lazy val answers = TableQuery[AnswerTable]

    def questionAnswers(qId: Int)(implicit session: JdbcBackend#Session) = answers.filter(_.quesId === qId).list

  }

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

  case class Question(id: Option[Int], title: String, catId: Int)

  class QuestionTable(tag: Tag) extends Table[Question](tag, "QUESTION"){

    def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)
    def title = column[String]("TITLE")
    def catId = column[Int]("CATEGORY_ID")
    def category = foreignKey("CATEGORY_FK", catId, Tables.categories)(_.id.get, onDelete=ForeignKeyAction.Cascade)
    override def * = (id, title, catId) <> (Question.tupled, Question.unapply)

  }

  case class Answer(id: Option[Int], title: String, isTrue: Boolean, quesId: Int)

  class AnswerTable(tag: Tag) extends Table[Answer](tag, "Answer"){

    def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)
    def title = column[String]("TITLE")
    def isTrue = column[Boolean]("IS_TRUE")
    def quesId = column[Int]("QUESTION_ID")
    def question = foreignKey("QUESTION_FK", quesId, Tables.questions)(_.id.get)

    override def * = (id, title, isTrue, quesId) <> (Answer.tupled, Answer.unapply)

  }


}
