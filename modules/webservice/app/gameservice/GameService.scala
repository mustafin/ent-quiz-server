package gameservice


import models.webservice._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * Created by Murat.
 * Game Service Logic
 */
object GameService {
  
  def startGame(user: GameUser): Future[GameData] = GameDAO.newGame(user).map(_.toGameData(user))


  def getRoundData(reply: Boolean, user: GameUser, gameId: Option[Long]): Future[Option[Seq[GameCategory]]] ={

    val roundNum = RoundDAO.roundNum(gameId)

    if(!reply){
      GameDAO.moveData().map(Some(_)) // firstMove {3 cat to choose}
    }else{
      RoundDAO.lastRound(gameId).flatMap(
        round =>
          if(round.isDefined)
            GameDAO.moveData(round.flatMap(_.categoryId)).map(Some(_)) // replyMove {1 cat}
          else Future.successful(None)
      )
    }

  }

  def submitRound() = ???

  def updateStatus() = ???

}
