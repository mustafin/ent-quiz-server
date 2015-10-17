package gameservice


import models.webservice._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

/**
 * Created by Murat.
 * Game Service Logic
 */
object GameService {

  def startGame(user: GameUser): Future[GameData] = GameDAO.newGame(user).map(_.toGameData(user))

  def getRoundData(user: GameUser, gameId: Option[Long]): Future[(Option[Long], Seq[GameCategory])] = {

    val count = RoundDAO.roundNum(gameId)
    val lastRound = RoundDAO.lastRound(gameId)
    lastRound.flatMap( round =>
      count.flatMap( c =>
        if (round.isDefined && round.get.replyMove(c))
          GameDAO.moveData(gameId, round).map(round.get.id -> _) // replyMove {1 cat}
        else{
          val g = GameDAO.moveData(gameId, round) // replyMove {3 cat}
          val r = RoundDAO.newRound(gameId)
          g.zip(r).map{case (gameCat, roundCur) => roundCur.id -> gameCat }
        }
      )
    )

  }

  def submitRound(gameRound: GameRound, user: GameUser) =
    GameDAO.find(gameRound.gameId).map {
      _ foreach (RoundDAO.submitRound(gameRound, _, user.id))
    }

  def updateStatus() = ???

}
