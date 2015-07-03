
import scala.concurrent.Future
import scala.slick.lifted.TableQuery
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.Tag
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

/**
 * Created by Murat.
 */
package object models {


  case class User(id: Option[Int], username: String, password: String)

  class UserTable(tag: Tag) extends Table[User](tag, "USER"){
    def id = column[Option[Int]]("ID", O.PrimaryKey)
    def username = column[String]("USERNAME")
    def password = column[String]("PASSWORD")

    override def * = (id, username, password) <> (User.tupled, User.unapply)
  }

  case class Category(id: Option[Int], name: String)


  class CategoryTable(tag: Tag) extends Table[Category](tag, "CATEGORY"){
    def id = column[Option[Int]]("ID", O.PrimaryKey)
    def name = column[String]("NAME")

    override def * = (id, name) <> (Category.tupled, Category.unapply)
  }

  val categories = TableQuery[Category]

  case class Question(id: Option[Int], name: String, catId: Int)

  class QuestionTable(tag: Tag) extends Table[Question](tag, "QUESTION"){

    def id = column[Option[Int]]("ID", O.PrimaryKey)
    def title = column[String]("TITLE")
    def catId = column[Int]("CATEGORY_ID")
    def category = foreignKey("CATEGORY_FK", catId, categories)(_.id.get)

    override def * = (id, title) <> (Question.tupled, Question.unapply)

  }

  val questions = TableQuery[Question]

}
