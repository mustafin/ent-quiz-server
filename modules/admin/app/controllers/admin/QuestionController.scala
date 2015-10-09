package controllers.admin

import javax.inject.Inject

import models.admin._
import org.apache.commons.codec.binary.Base32
import play.api.Play
import play.api.Play.current
import play.api.i18n.{MessagesApi, I18nSupport}
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.Controller
import slick.driver.JdbcProfile
import views.html.admin

import scala.slick.driver.MySQLDriver.simple._

/**
 * Created by Murat.
 */
class QuestionController  @Inject() (val messagesApi: MessagesApi) extends Controller with Secured with I18nSupport {

  lazy val questions = TableQuery[QuestionTable]
  lazy val answers = TableQuery[AnswerTable]
  lazy val db = DatabaseConfigProvider.get[JdbcProfile](Play.current).db

  val form = Form(
    mapping(
      "id" -> ignored[Option[Long]](None),
      "title" -> text,
      "catId" -> optional(longNumber),
      "img" -> optional(text),
      "answers" -> seq(
        mapping(
          "id" -> optional(longNumber),
          "title" -> text,
          "isTrue" -> boolean,
          "quesId" -> optional(longNumber),
          "img" -> optional(text)
        )({ case (a, b, c, d, e) => Answer(a, b, c, d.getOrElse(0), e.getOrElse("")) })
         ({ case (Answer(a, b, c, d, e)) => Option((a, b, c, Option(d), Option(e))) })
      )
    )({ case (a, b, c, d, e) => (Question(a, b, c.getOrElse(0), d.getOrElse("")), e) })
     ({ case (Question(a, b, c, d), e) => Option((a, b, Option(c), Option(d), e)) })
  )

  def list(catId: Long) = Authenticated { implicit rs =>
    val list = db withSession { implicit session => questions.filter(_.catId === catId).list }

    Ok(admin.question.list(form, list, catId))
  }

  def add(newCatId: Long) = Authenticated(parse.multipartFormData) { implicit rs =>
    form.bindFromRequest.fold(
      formWithErrors => {
        val catQuestions = db.withSession { implicit session => questions.filter(_.catId === newCatId).list }
        println(formWithErrors.errorsAsJson)
        BadRequest(admin.question.list(formWithErrors, catQuestions, newCatId))
      },
      question => {
        val fileNames = rs.body.files.map {
          img =>
            import java.io.File
            val uploadPath = current.configuration.getString("application.upload")

            val genName = new Base32().encode(System.currentTimeMillis() / 1000) + "." +
              img.filename.replaceFirst("^[\\s\\S]+[.]", "")
            img.ref.moveTo(new File(uploadPath.getOrElse("") + File.separator + genName))
            img.key -> genName
        }

        val (quest, answerSeq) = question
        val questionFileName = fileNames.map(_._2).find(_ == "picture").getOrElse("")

        val qId = db.withSession { implicit session =>
          (questions returning questions.map(_.id)) += quest.copy(catId = newCatId, img = questionFileName) }
        db.withSession { implicit session => answers ++= answerSeq.map(_.copy(quesId = qId.getOrElse(0))) }
        Redirect(routes.QuestionController.list(newCatId))
      }
    )
  }

  def edit(id: Long) = Authenticated { implicit rs =>
    val question = db.withSession { implicit session =>
      questions.filter(_.id === id).firstOption }
    if (question.isDefined)
      Ok(admin.question.edit(question.get, form.fill((question.get, Tables.questionAnswers(id)(db)))))
    else NotFound("Not FOund")
  }

  def a = Authenticated{ rs =>
    Ok("asf")
  }

  def updateQuestion(id: Long) = Authenticated(parse.multipartFormData) { implicit rs =>
    form.bindFromRequest.fold(
      errorForm => {
        println(errorForm.errorsAsJson)
        Redirect(routes.QuestionController.edit(id))
      },
      data => {
        val fileName = rs.body.file("picture").map {
          img =>
            import java.io.File
            val uploadPath = current.configuration.getString("application.upload")
            img.ref.moveTo(new File(uploadPath.getOrElse("") + File.separator + img.filename))
            img.filename
        }.getOrElse("")

        val questionToUpdate: Question = data._1.copy(id = Some(id), img = fileName)

        db.withSession { implicit session =>
          questions.filter(_.id === id).update(questionToUpdate)
          data._2.map {
            case answer if answer.id.isDefined => answers.filter(_.id === answer.id).update(answer)
            case answer => answers += answer
          }
        }
        Redirect(routes.QuestionController.list(data._1.catId))
      }
    )
  }

  def delete(id: Long) = Authenticated { implicit rs =>
    val q = questions.filter(_.id === id)
    val quest = db.withSession { implicit session => q.firstOption.get }
    db.withSession { implicit session => q.delete }
    Redirect(routes.QuestionController.list(quest.catId))
  }

  def deleteAnswer(id: Long) = Authenticated { implicit rs =>
    val a = answers.filter(_.id === id)
    val answer = db.withSession { implicit session => a.firstOption.get }
    db.withSession { implicit session => a.delete }
    Redirect(routes.QuestionController.edit(answer.quesId))
  }

  def deleteImage(id: Long) = Authenticated { implicit rs =>
    val q = for{ question <- questions if question.id === id} yield question.img
    db.withSession { implicit session =>
      q.firstOption foreach {
        image =>
          import java.io.File
          val path = current.configuration.getString("application.upload")
          val file = new File(path.map(_ + File.separator).getOrElse("") + image)
          if(file.exists()) file.delete()
      }
    }
    db.withSession { implicit session => q.update("") }
    Redirect(routes.QuestionController.edit(id))
  }

}
