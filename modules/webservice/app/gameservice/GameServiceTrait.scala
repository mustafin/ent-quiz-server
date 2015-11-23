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

  def getRoundData(user: GameUser, game: Game): Future[JsValue]

  def submitRound(gameRound: GameRound, user: GameUser): Future[Move]

}

object GameServiceImpl extends GameServiceTrait{

  override def startGameOrJoin(user: GameUser): Future[JsValue] =
    newGameRoundFlat(user){ (game, round) => Move(round, game, user).serialized }

  override def startGameWithOpponent(user: GameUser, opponent: Option[Long]): Future[JsValue] =
    for{
      game <- GameDAO.newGame(user, opponent)
      round <- RoundDAO.newRound(game.id)
      move <- Move(round, game, user).serialized
    } yield {
      move
    }

  override def submitRound(gameRound: GameRound, user: GameUser): Future[Move] = {
    val moveFut = findGameRound(gameRound.gameId, gameRound.roundId) { (game, round) =>
      Move(round, game, user, Some(gameRound))
    }
    moveFut.foreach{ _.submit(gameRound) }
    moveFut
  }

  override def getRoundData(user: GameUser, game: Game): Future[JsValue] = {
    findGameRoundFlat(game.id){ (game, round) =>
      Move(round, game, user).serialized
    }
  }


  private def newGameRound[T](user: GameUser)(f: (Game, Round) => T): Future[T]  = {
    val tupFut = for {
      game <- GameDAO.newGameOrJoin(user)
      lastRound <- RoundDAO.lastRound(game.id)
    } yield (game, lastRound)

    tupFut.flatMap {
      case (g, l) =>
        if(l.isDefined) Future.successful(f(g,l.get))
        else RoundDAO.newRound(g.id).map(f(g, _))
    }
  }

  private def newGameRoundFlat[T](user: GameUser)(f: (Game, Round) => Future[T]): Future[T]  = {
    val tupFut = for {
      game <- GameDAO.newGameOrJoin(user)
      lastRound <- RoundDAO.lastRound(game.id)
    } yield (game, lastRound)

    tupFut.flatMap {
      case (g, l) =>
        if(l.isDefined) f(g, l.get)
        else RoundDAO.newRound(g.id).flatMap(f(g, _))
    }
  }

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

  private def findGameRoundFlat[T](gameId: Option[Long], roundId: Option[Long] = None)(f: (Game, Round) => Future[T]): Future[T] = {
    println(gameId)
    println(roundId)
    val d = for{
      roundOp <- roundId.fold(RoundDAO.lastRound(gameId))(rId => RoundDAO.find(Some(rId)))
      gameOp <- GameDAO.find(gameId)
    } yield (roundOp, gameOp)
    d.flatMap{ case (r, g) =>
      println(r)
      println(g)
      val op = for{
        round <- r
        game <- g
      } yield{
        f(game, round)
      }
      op.getOrElse(throw new Exception("round or game not found"))
    }

  }

}

