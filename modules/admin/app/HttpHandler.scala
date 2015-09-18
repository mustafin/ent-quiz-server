import javax.inject._

import play.api.http.DefaultHttpErrorHandler
import play.api._
import play.api.http.Status
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import play.api.mvc.Results._
import play.api.routing.Router
import scala.concurrent.Future

class HttpHandler @Inject() (
    env: Environment,
    config: Configuration,
    sourceMapper: OptionalSourceMapper,
    router: Provider[Router],
    val messagesApi: MessagesApi
  ) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) with I18nSupport {

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = Future.successful(

    if(statusCode == Status.BAD_REQUEST) {
      BadRequest("Bad Request: " + message)
    }else if(statusCode == Status.NOT_FOUND){
      NotFound(views.html.admin.errors.onHandlerNotFound(request))
    }else if(statusCode == Status.UNAUTHORIZED){
      Unauthorized("Unauthorized: " + message)
    }else{
      BadRequest(statusCode + ": " + message)
    }
  )

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = Future.successful{
    InternalServerError(views.html.admin.errors.onError(exception))
  }

}