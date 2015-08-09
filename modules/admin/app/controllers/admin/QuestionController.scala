package controllers.admin

import models.admin._
import org.apache.commons.codec.binary.Base32
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.DB
import play.api.db.slick.{Database => _}
import play.api.mvc.Controller
import views.html.admin

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.lifted.TableQuery

/**
 * Created by Murat.
 */
object QuestionController extends Controller with Secured {

  lazy val questions = TableQuery[QuestionTable]
  lazy val answers = TableQuery[AnswerTable]
  lazy val db = Database.forDataSource(DB.getDataSource())

  val form = Form(
    mapping(
      "id" -> ignored[Option[Int]](None),
      "title" -> text,
      "catId" -> optional(number),
      "img" -> optional(text),
      "answers" -> seq(
        mapping(
          "id" -> optional(number),
          "title" -> text,
          "isTrue" -> boolean,
          "quesId" -> optional(number),
          "img" -> optional(text)
        )({ case (a, b, c, d, e) => Answer(a, b, c, d.getOrElse(0), e.getOrElse("")) })
         ({ case (Answer(a, b, c, d, e)) => Option((a, b, c, Option(d), Option(e))) })
      )
    )({ case (a, b, c, d, e) => (Question(a, b, c.getOrElse(0), d.getOrElse("")), e) })
     ({ case (Question(a, b, c, d), e) => Option((a, b, Option(c), Option(d), e)) })
  )

  def list(catId: Int) = Authenticated { implicit rs =>
    val list = db.withSession { implicit session => questions.filter(_.catId === catId).list }
    Ok(admin.question.list(form, list, catId))
  }

  def add(newCatId: Int) = Authenticated(parse.multipartFormData) { implicit rs =>
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

  def edit(id: Int) = Authenticated { implicit rs =>
    val question = db.withSession { implicit session =>
      questions.filter(_.id === id).firstOption }
    if (question.isDefined)
      Ok(admin.question.edit(question.get, form.fill((question.get, Tables.questionAnswers(id)(db.createSession())))))
    else NotFound("Not FOund")
  }

  def updateQuestion(id: Int) = Authenticated(parse.multipartFormData) { implicit rs =>
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

  def delete(id: Int) = Authenticated { implicit rs =>
    val q = questions.filter(_.id === id)
    val quest = db.withSession { implicit session => q.firstOption.get }
    db.withSession { implicit session => q.delete }
    Redirect(routes.QuestionController.list(quest.catId))
  }

  def deleteAnswer(id: Int) = Authenticated { implicit rs =>
    val a = answers.filter(_.id === id)
    val answer = db.withSession { implicit session => a.firstOption.get }
    db.withSession { implicit session => a.delete }
    Redirect(routes.QuestionController.edit(answer.quesId))
  }

  def deleteImage(id: Int) = Authenticated { implicit rs =>
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
