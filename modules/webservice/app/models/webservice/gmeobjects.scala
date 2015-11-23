package models.webservice

import models.admin.{Category, Answer, Question}
import util.Extensions._


/**
 * Created by Murat.
 */

case class GameQuestion(ques: Question, answers: Seq[Answer], opAns: Option[Long])
case class GameCategory(category: Category, questions: Seq[GameQuestion])
case class GameData(gameId: Option[Long], user: Option[GameUser], opponent: Option[GameUser], scoreOne: Int, scoreTwo: Int){
  def opponentStart = opponent.isDefined
}

case class GameRound(roundId: Option[Long], gameId: Option[Long], roundNum: Option[Int], catId: Option[Long],
                     q1Id: Option[Long], q2Id: Option[Long], q3Id: Option[Long],
                     a1Id: Option[Long], a2Id: Option[Long], a3Id: Option[Long]){
  def answers = Set(a1Id, a2Id, a3Id).flatten

}

