package models.webservice

import models.admin.{Category, Answer, Question}

/**
 * Created by Murat.
 */

case class GameQuestion(ques: Question, answers: Seq[Answer], opAns: Option[Long])
case class GameCategory(category: Category, questions: Seq[GameQuestion])
case class GameData(gameId: Option[Long], user: Option[GameUser], opponent: Option[GameUser], scoreOne: Int, scoreTwo: Int){
  def opponentStart = opponent.isDefined
}

case class GameRound(id: Option[Long], count: Int)

