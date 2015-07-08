package controllers

import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.DB
import play.api.db.slick.{Database => _, _}
import scala.slick.driver.MySQLDriver.simple._
import play.api.mvc.Controller
import play.api.Play.current


import scala.slick.lifted.TableQuery

/**
 * Created by Murat.
 */
object QuestionController extends Controller with Secured{


  lazy val questions = TableQuery[QuestionTable]
  lazy val answers = TableQuery[AnswerTable]
  lazy val db = Database.forDataSource(DB.getDataSource())


  val form = Form(
    mapping(
      "id" -> ignored[Option[Int]](None),
      "title" -> text,
      "catId" -> optional(number),
      "answers" -> seq(
        mapping(
          "id" -> optional(number),
          "title" -> text,
          "isTrue" -> boolean,
          "quesId" -> optional(number)
        )({case (a,b,c,d) => Answer(a, b, c, d.getOrElse(0))})({case (Answer(a,b, c, d)) => Option((a,b,c,Option(d)))})
      )
    )({case (a,b,c,d) => (Question(a,b,c.getOrElse(0)), d)})({case (Question(a,b,c), d) => Option((a,b,Option(c),d))})
  )

  def list(catId: Int) = withAuth { username => implicit rs =>
    val list = db.withSession { implicit session => questions.filter(_.catId === catId).list }
    Ok(views.html.question.list(form, list, catId))
  }

  def add(newCatId: Int) = withAuth { username => implicit rs =>
    form.bindFromRequest.fold(
      formWithErrors => {
        val catQuestions = db.withSession { implicit session => questions.filter(_.catId === newCatId).list}
        BadRequest(views.html.question.list(formWithErrors, catQuestions, newCatId))
      },
      question => {
        val qId = db.withSession { implicit session => (questions returning questions.map(_.id)) += question._1.copy(catId = newCatId)}
        db.withSession { implicit session => answers ++= question._2.map(_.copy(quesId = qId.getOrElse(0)))}
        Redirect(routes.QuestionController.list(newCatId))
      }
    )
  }

  def edit(id: Int) = withAuth { username => implicit rs =>
    val question = db.withSession { implicit session => questions.filter(_.id === id).firstOption}
    if(question.isDefined)
      Ok(views.html.question.edit(question.get, form.fill((question.get, Tables.questionAnswers(id)(db.createSession())))))
    else NotFound("Not FOund")
  }

  def updateQuestion(id: Int) = withAuth { username => implicit rs =>
    form.bindFromRequest.fold(
      errorForm =>{
        println(errorForm.errorsAsJson)
        Redirect(routes.QuestionController.edit(id))
      },
      data=>  {
        val questionToUpdate: Question = data._1.copy(Some(id))
        println(data._2.mkString("\n"))

        db.withSession { implicit session =>
          questions.filter(_.id === id).update(questionToUpdate)
          data._2.map{
            case answer if answer.id.isDefined => answers.filter(_.id === answer.id).update(answer)
            case answer => answers += answer
          }
        }

        Redirect(routes.QuestionController.list(data._1.catId))
      }
    )
  }

  def delete(id: Int) = withAuth { username => implicit rs =>
    val q = questions.filter(_.id === id)
    val quest = db.withSession { implicit session => q.firstOption.get}
    db.withSession { implicit session => q.delete}
    Redirect(routes.QuestionController.list(quest.catId))
  }

  def deleteAnswer(id: Int) = withAuth { username => implicit rs =>
    val a = answers.filter(_.id === id)
    val answer = db.withSession { implicit session => a.firstOption.get}
    db.withSession { implicit session => a.delete}
    Redirect(routes.QuestionController.edit(answer.quesId))
  }

}
