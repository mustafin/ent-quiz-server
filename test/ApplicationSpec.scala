import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.test.{FakeRequest, WithApplication}
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

    "send 404 on a bad request" in new WithApplication{
      assert(2 == 2)
    }

  }
}
