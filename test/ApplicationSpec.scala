import models.admin.Tables
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.test.WithApplication
import slick.driver.JdbcProfile
import slick.lifted.SimpleFunction
import play.api.libs.concurrent.Execution.Implicits._

import slick.driver.MySQLDriver.api._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  "Application" should {

//    "send 404 on a bad request" in new WithApplication{
//      route(FakeRequest(GET, "/boum")) must beNone
//    }
//
//
//    "add user to database" in new WithApplication() {
//      val user = new User(None, "admin", "12435")
//      UserDAO.create(user)
//    }

//    "should generate token" in new WithApplication() {
//      val token = Token.createToken(GameUser(Some(1), "murat", "", Some(1200)))
//      println(token)
//    }


    "sasd" in new WithApplication() {

      val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

      val db = dbConfig.db
      val rand = SimpleFunction.nullary[Double]("rand")
      val categories = for{
        (cat, ques) <- Tables.categories.sortBy(x => rand).take(3).withQuestions
      }yield (cat, ques)

      db.run(categories.result).foreach {
        _ foreach {
          case (x, y) => println(x, y)

        }

      }


    }



  }
}
