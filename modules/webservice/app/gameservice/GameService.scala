package gameservice

import models.webservice._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by Murat.
 * Game Service Logic
 */
@Deprecated
object GameService {

  def startGame(user: GameUser) = GameDAO.newGame(user)

  def getRoundData(user: GameUser, game: Game): Future[(Option[Long], Seq[GameCategory])] = {

    val lastRound = RoundDAO.lastRound(game.id)
      lastRound.flatMap( round =>
        if(game.myMove(user))
          if (round.isDefined && !round.get.finished)
            GameDAO.moveData(game, round, reply = true).map(round.get.id -> _) // replyMove {1 cat}
          else{
            val g = GameDAO.moveData(game, round, reply = false) // replyMove {3 cat}
            val r = RoundDAO.newRound(game.id)
            g.zip(r).map{case (gameCat, roundCur) => roundCur.id -> gameCat }
          }
        else
          Future.successful((round.flatMap(_.id), Nil))
      )
  }

  def submitRound(gameRound: GameRound, user: GameUser) =

    GameDAO.find(gameRound.gameId).flatMap {
      case Some(g) => RoundDAO.submitRound(gameRound, g, user.id)
      case None => Future.failed(new Exception("game not found"))
    }

  def updateStatus() = ???

}
