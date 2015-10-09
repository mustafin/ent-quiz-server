package gameservice


import models.webservice._

import scala.concurrent.Future


/**
 * Created by Murat.
 * Game Service Logic
 */
object GameService {
  
  def startGame(user: GameUser): Future[GameData] = {

    for{
      game <- GameDAO.newGame(user)
    }yield{
      val opponent = game.userOneId != user.id

      val firstUser = if(game.userOneId == user.id) Some(user) else GameUserDAO.find(game.userOneId)
      val secondUser = if(game.userTwoId == user.id) Some(user) else GameUserDAO.find(game.userTwoId)
      GameData(game.id, firstUser, secondUser, game.scoreOne, game.scoreTwo)
    }

  }

  def getRoundData(reply: Boolean, user: GameUser, gameId: Option[Int])={

    val roundNum = RoundDAO.roundNum(gameId)

    if(!reply){
      GameDAO.moveData() // firstMove {3 cat to choose}
    }else{
      
      for{
        roundOption <- RoundDAO.lastRound(gameId)
        round <- roundOption
      }yield{
        GameDAO.moveData(round.categoryId) // replyMove {1 cat}
      }
    }
  }

  def submitRound() = ???

  def updateStatus() = ???


}
