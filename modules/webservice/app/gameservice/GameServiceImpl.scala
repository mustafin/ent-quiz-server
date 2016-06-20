package gameservice

import models.webservice.{GameRound, RoundDAO, GameDAO, GameUser}
import play.api.libs.json.JsValue

import scala.concurrent.Future

/**
  * Created by musta on 2016-06-20.
  */
trait GameServiceImpl extends GameServiceTrait{

  override def startGameOrJoin(user: GameUser): Future[JsValue] =
    for {
      game <- GameDAO.newGameOrJoin(user)
      round <- RoundDAO.get(game.id)
      out <- Move(round, game, user).serialized
    } yield out

  override def startGameWithOpponent(user: GameUser, opponent: Option[Long]): Future[JsValue] =
    for{
      game <- GameDAO.newGame(user, opponent)
      round <- RoundDAO.newRound(game.id)
      move <- Move(round, game, user).serialized
    } yield move

  override def submitRound(gameRound: GameRound, user: GameUser): Future[Unit] = {
    val moveFut = for{
      roundOp <- RoundDAO.find(gameRound.roundId)
      gameOp <- GameDAO.find(gameRound.gameId)
    } yield {
      val op = for{
        round <- roundOp
        game <- gameOp
      } yield{
        Move(round, game, user, Some(gameRound))
      }
      op.getOrElse(throw new Exception("round or game not found"))
    }
    moveFut.map{ _.submit(gameRound) }
  }

  override def getRoundData(user: GameUser, gameId: Long): Future[JsValue] = {
    GameDAO.find(Some(gameId)).flatMap{
      _.map{
        game => if(game.finished){
          Move.finished(game, user)
        }else{
          RoundDAO.get(Some(gameId)).flatMap(Move(_, game, user).serialized)
        }
      }.getOrElse(throw new Exception("round or game not found"))
    }
  }

}

