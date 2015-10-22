package gameservice

import models.webservice._
import play.api.libs.json.JsValue

import scala.concurrent.Future

/**
 * Created by Murat.
 */
trait GameServiceTrait {

  def startGame(user: GameUser): Future[JsValue]

  def getRoundData(user: GameUser, game: Game): Future[JsValue]

  def submitRound(gameRound: GameRound, user: GameUser): Future[Move]

}

object GameServiceImpl extends GameServiceTrait{

  override def startGame(user: GameUser): Future[JsValue] =
    newGameRound(user){ (game, round) =>
      Move(round, game, user).serialized
  }

  override def submitRound(gameRound: GameRound, user: GameUser): Future[Move] =
    findGameRound(gameRound.roundId, gameRound.gameId){ (game, round) =>
      Move(round, game, user, Some(gameRound))
  }



  override def getRoundData(user: GameUser, game: Game): Future[JsValue] = {
    findGameRound(game.id){ (game, round) =>
      Move(round, game, user).serialized
    }
  }

  private def newGameRound[T](user: GameUser)(f: (Game, Round) => T): Future[T]  =
    for{
      game <- GameDAO.newGame(user)
      round <- RoundDAO.newRound(game.id)
  } yield f(game, round)


  private def findGameRound[T](gameId: Option[Long], roundId: Option[Long] = None)(f: (Game, Round) => T): Future[T] =
    for{
      roundOp <- roundId.fold(RoundDAO.lastRound(gameId))(rId => RoundDAO.find(Some(rId)))
      gameOp <- GameDAO.find(gameId)
    } yield {
      val op = for{
        round <- roundOp
        game <- gameOp
      } yield{
          f(game, round)
      }
      op.getOrElse(throw new Exception("round or game not found"))
  }

}

