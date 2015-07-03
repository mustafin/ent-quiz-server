import scala.slick.lifted.TableQuery
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.Tag

/**
 * Created by Murat.
 */
package object models {

  object Tables{
    val users = TableQuery[UserTable]
    val categories = TableQuery[CategoryTable]
  }

  case class User(id: Option[Int], username: String, password: String)
  case class Category(id: Option[Int], name: String)

  class UserTable(tag: Tag) extends Table[User](tag, "USER"){
    def id = column[Option[Int]]("ID", O.PrimaryKey)
    def username = column[String]("USERNAME")
    def password = column[String]("PASSWORD")

    override def * = (id, username, password) <> (User.tupled, User.unapply)
  }

  class CategoryTable(tag: Tag) extends Table[Category](tag, "CATEGORY"){
    def id = column[Option[Int]]("ID", O.PrimaryKey)
    def name = column[String]("NAME")

    override def * = (id, name) <> (Category.tupled, Category.unapply)
  }

}
