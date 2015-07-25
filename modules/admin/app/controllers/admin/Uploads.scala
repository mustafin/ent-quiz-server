package controllers.admin

import java.io.File

import play.api.Play.current
import play.api.mvc._
import play.api.{Configuration, Play}

object Uploads extends Controller {

  import play.api.http.ContentTypes
  import play.api.libs.MimeTypes
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  import play.api.libs.iteratee.Enumerator

  def at(file: String) = Action { implicit request =>
    val contentUrl = Play.configuration.getString("application.upload")
    val fileResUri = contentUrl.map(_ + File.separator).getOrElse("")+file
    val mimeType: String = MimeTypes.forFileName( fileResUri ).fold(ContentTypes.BINARY)(addCharsetIfNeeded)
    val serveFile = new File(fileResUri)
    println(fileResUri)
    if( serveFile.exists() ){
      val fileContent: Enumerator[Array[Byte]] = Enumerator.fromFile( serveFile )
      //Ok.sendFile(serveFile).as( mimeType )
      val response = Result(
        ResponseHeader(
          OK,
          Map(
            CONTENT_LENGTH -> serveFile.length.toString,
            CONTENT_TYPE -> mimeType
          )
        ),
        fileContent
      )
      response
    }
    else {
      NotFound
    }
  }

  def addCharsetIfNeeded(mimeType: String): String =
    if (MimeTypes.isText(mimeType)) s"$mimeType; charset=$defaultCharSet" else mimeType

  lazy val defaultCharSet = config(_.getString("default.charset")).getOrElse("utf-8")

  def config[T](lookup: Configuration => Option[T]): Option[T] = for {
    app <- Play.maybeApplication
    value <- lookup(app.configuration)
  } yield value


}
