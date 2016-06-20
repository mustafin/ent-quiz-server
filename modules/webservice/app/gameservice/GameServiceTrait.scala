package gameservice

import models.webservice._
import play.api.libs.json.JsValue
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

/**
 * Created by Murat.
 */
trait GameServiceTrait {

  def startGameOrJoin(user: GameUser): Future[JsValue]
  
  def startGameWithOpponent(user: GameUser, opponent: Option[Long]): Future[JsValue]

  def getRoundData(user: GameUser, gameId: Long): Future[JsValue]

  def submitRound(gameRound: GameRound, user: GameUser): Future[Unit]

}

