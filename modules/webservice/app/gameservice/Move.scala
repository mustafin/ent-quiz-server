package gameservice

import models.admin.{Question, Category, Answer}
import models.webservice._
import play.api.libs.json.{JsObject, Json, JsValue}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

/**
 * Created by Murat.
 */
sealed trait Move {
  def isReply: Boolean
  def myMove: Boolean
  def isNew: Boolean
  def userAnswers: Map[Option[Long], Option[Long]]
  def submit(gameRound: GameRound): Unit
  def formattedRoundData: Future[Seq[GameCategory]]
  def serialized: Future[JsValue]

}

object Move{
  def apply(round: Round, game: Game, user: GameUser, gr: Option[GameRound] = None): Move = {

    val newRound = if(gr.isDefined){
      if(game by user) round.copy(uoneAnsOneId = gr.get.a1Id, uoneAnsTwoId = gr.get.a2Id, uoneAnsThreeId = gr.get.a3Id)
      else if (game opp user) round.copy(utwoAnsOneId = gr.get.a1Id, utwoAnsTwoId = gr.get.a2Id, utwoAnsThreeId = gr.get.a3Id)
      else round
    } else round

    if(round.empty) new FirstMove(newRound, game, user)
    else new ReplyMove(newRound, game, user)
  }
}

abstract class AbstractMove(round: Round, game: Game, user: GameUser) extends Move{

  import models.webservice.GameDAO.Implicits._

  def submit(gameRound: GameRound): Unit = {
    if(game by user) RoundDAO.countAndSaveScores(gameRound.answers, game, left = true)
    else if(game opp user) RoundDAO.countAndSaveScores(gameRound.answers, game, left = true)
    RoundDAO.submitRound(gameRound, game, user.id)
  }

  def myMove: Boolean = game.myMove(user)

  def isNew: Boolean = round.empty

  def userAnswers: Map[Option[Long], Option[Long]] = round.userAnswers(game)

  def formattedRoundData: Future[Seq[GameCategory]] = {
    if(game.myMove(user)) {
      val (cat, ques) = roundData
      formatData(cat, ques)
    } else Future.successful(Nil)
  }

  def roundData: (Future[Seq[Category]], Future[Seq[(Question, Option[Answer])]])

  def formatData(categoriesFut: Future[Seq[Category]],
                 quesAndAnswersFut: Future[Seq[(Question, Option[Answer])]]): Future[Seq[GameCategory]] = {
    for{
      categories <- categoriesFut
      items <- quesAndAnswersFut
    }yield{
      categories.map(
        x => {
          //converting List[Tuple3] to List[k -> (k -> v)]
          val gameQuestions = items.filter(_._1.catId == x.id.get).groupBy(_._1).mapValues(_.map(_._2))
            .map{
            case (qes, ans) => GameQuestion(qes, ans.flatten, userAnswers(qes.id))
          }.toVector
          GameCategory(x, gameQuestions)
        }
      )
    }
  }

  def serialized = {
    for {
      rData <- formattedRoundData
      gameData <- game.toGameData(user)
    } yield {
      Json.toJson(gameData).as[JsObject] +
        ("roundId" -> Json.toJson(round.id)) +
        ("data" -> Json.toJson(rData))
    }
  }

}

case class FirstMove(round: Round, game: Game, user: GameUser) extends AbstractMove(round, game, user){

  def isReply: Boolean = false

  def roundData:(Future[Seq[Category]], Future[Seq[(Question, Option[Answer])]]) = {
    GameDAO.multipleCategoriesData(game, 3)
  }

}

case class ReplyMove(round: Round, game: Game, user: GameUser) extends AbstractMove(round, game, user){

  def isReply: Boolean = true

  def roundData:(Future[Seq[Category]], Future[Seq[(Question, Option[Answer])]]) = {
    GameDAO.oneCategoryData(round)
  }

}

/* START
{
    "gameId": 34,
    "user": {
        "id": 3,
        "username": "askar",
        "password": "",
        "rating": 1200
    },
    "opponent": {
        "id": 1,
        "username": "murat",
        "password": "",
        "rating": 1200
    },
    "scoreOne": 0,
    "scoreTwo": 0,
    "data": [
        {
            "category": {
                "id": 3,
                "name": "History"
            },
            "questions": [
                {
                    "ques": {
                        "id": 3,
                        "title": "gdrhr",
                        "catId": 3,
                        "img": ""
                    },
                    "answers": [],
                    "opAns": 1
                },
                {
                    "ques": {
                        "id": 45,
                        "title": "sdgsdg",
                        "catId": 3,
                        "img": ""
                    },
                    "answers": [
                        {
                            "id": 36,
                            "title": "sdgsdg",
                            "isTrue": true,
                            "quesId": 45,
                            "img": ""
                        },
                        {
                            "id": 37,
                            "title": "sdgsdg",
                            "isTrue": false,
                            "quesId": 45,
                            "img": ""
                        }
                    ],
                    "opAns": 36
                },
                {
                    "ques": {
                        "id": 46,
                        "title": "sdgdsgsdg",
                        "catId": 3,
                        "img": ""
                    },
                    "answers": [
                        {
                            "id": 38,
                            "title": "sdgsdgsdg",
                            "isTrue": false,
                            "quesId": 46,
                            "img": ""
                        }
                    ],
                    "opAns": 38
                }
            ]
        }
    ]
}
 */

/* OPPONENT START
{
    "gameId": 36,
    "user": {
        "id": 1,
        "username": "murat",
        "password": "",
        "rating": 1200
    },
    "opponent": {
        "id": 3,
        "username": "askar",
        "password": "",
        "rating": 1200
    },
    "scoreOne": 0,
    "scoreTwo": 0,
    "data": []
}
*/


/* ANSWER
  {"gameId": 34,
  "roundNum":1,
  "catId":3,
  "q1Id":3,
  "q2Id":45,
  "q3Id":46,
  "a1Id":1,
  "a2Id":36,
  "a3Id":38}
 */

