import javax.inject.Inject

import play.api.http.{HttpFilters, HttpConfiguration, HttpErrorHandler, DefaultHttpRequestHandler}
import play.api.mvc.RequestHeader

/**
 * Created by Murat.
 */
class RequestHandler @Inject() (errorHandler: HttpErrorHandler,
                                           configuration: HttpConfiguration, filters: HttpFilters,
                                           adminRouter: admin.Routes, wsRouter: webservice.Routes
                                            ) extends DefaultHttpRequestHandler(
  wsRouter, errorHandler, configuration, filters
) {

  private def getSubdomain (request: RequestHeader) = request.domain.replaceFirst("[\\.]?[^\\.]+[\\.][^\\.]+$", "")

  override def routeRequest(request: RequestHeader) = {
    getSubdomain(request) match {
      case "admin" => adminRouter.routes.lift(request)
      case _ => wsRouter.routes.lift(request)
    }
  }
}