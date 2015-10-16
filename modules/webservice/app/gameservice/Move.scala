package gameservice

import models.webservice.{GameRound, Round, GameUser}
import play.api.libs.json.JsValue

/**
 * Created by Murat.
 */
sealed trait Move {

  def isReply: Boolean
  def submit(): Unit
  def get(): JsValue
  def serialized(): JsValue

}

object Move{
//  def from(): Move = {
//
//  }
}

class MyMove(game: GameRound, user: GameUser) extends Move{

  override def isReply: Boolean = false

  override def submit(): Unit = GameService.submitRound(game, user)

  override def get(): JsValue = ???

  override def serialized(): JsValue = ???

}

class OpponentMove(game: GameRound, user: GameUser) extends Move{

  override def isReply: Boolean = true

  override def submit(): Unit = GameService.submitRound(game, user)

  override def get(): JsValue = ???

  override def serialized(): JsValue = ???

}


