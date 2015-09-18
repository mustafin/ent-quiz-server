package controllers.admin

import javax.inject.Inject

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Controller

class Application @Inject() (val messagesApi: MessagesApi) extends Controller with Secured with I18nSupport{

  def index() = withAuth{ username => implicit rs =>
    Ok(views.html.admin.index("Your new application is ready."))
  }

}