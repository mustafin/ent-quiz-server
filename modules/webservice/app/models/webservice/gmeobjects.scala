package models.webservice

import models.admin.{Category, Answer, Question}

/**
 * Created by Murat.
 */

case class GameQuestion(ques: Question, answers: Seq[Answer], opAns: Option[Long])
case class GameCategory(category: Category, questions: Seq[GameQuestion])
case class GameData(id: Option[Long], userOne: Option[GameUser], userTwo: Option[GameUser], scoreOne: Int, scoreTwo: Int){
  def opponentStart = userTwo.isDefined
}
case class GameRound(id: Option[Long], count: Int)

