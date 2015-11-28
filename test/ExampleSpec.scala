//import org.specs2.control.Ok

import helpers.Token
import models.webservice.{GameUserDAO, GameDAO, GameUser, GameCategory}
import play.api.libs.json.Json
import play.api.mvc.{Results, Action, EssentialAction}
import play.api.test.{FakeApplication, FakeRequest, PlaySpecification, WithApplication}
import play.api.mvc.Results._
import models.webservice.GameDAO.Implicits._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

/**
 * Created by Murat.
 */
object ExampleSpec extends PlaySpecification with Results{



  "An essential action" should {
//    val app = new FakeApplication()
    "can parse a JSON body" in new WithApplication {

      val user = Await.result(GameUserDAO.find(Some(1)), Duration.Inf)
      val token = Token.createToken(user.get)
      //val t = "eyJhbGciOiJIbWFjU0hBMjU2IiwidHlwIjoiSldUIn0.eyJpZCI6MSwidXNlcm5hbWUiOiJtdXJhdCIsInBhc3N3b3JkIjoiIiwicmF0aW5nIjoxMjAwfQ.Ae-_vT0pK--_ve-_ve-_ve-_vTA777-9Fm91a--_vX4477-9ARXvv73vv71977-9KVzvv70sbl8"
      val authHeader = "Bearer "+token

      val headers = "Authorization" -> authHeader :: "Content-Type" -> "text/json" :: Nil

      val start = FakeRequest(GET, "/start").withHeaders(headers: _*)
      val Some(res) = route(start)
      val js = contentAsJson(res)
      println(s"*******\n$js\n*******")
//      (js \ "data").as[Seq[GameCategory]] mustNotEqual null
      status(res) mustEqual OK
      (js \ "opAns") mustNotEqual null


//      val request = FakeRequest(POST, "/").withJsonBody(Json.parse("""{ "field": "value" }"""))
//        .withHeaders(
//        "Authorization" -> authHeader,
//        "Content-Type" -> "text/json"
//      )

    }
  }
}