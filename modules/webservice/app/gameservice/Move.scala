package gameservice

import models.admin.{Question, Category, Answer}
import models.webservice._
import play.api.libs.json.{JsObject, Json, JsValue}
import scala.concurrent.ExecutionContext.Implicits.global
import models.webservice.GameDAO.Implicits._
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

  def finished(game: Game, user: GameUser) = {
    game.toGameData(user).map{
      gameData => Json.toJson(gameData).as[JsObject] ++ Json.obj("finished" -> true)
    }
  }

}

abstract class AbstractMove(round: Round, game: Game, user: GameUser) extends Move{

  import play.api.libs.json._
  import play.api.libs.json.Reads._
  import play.api.libs.json.Json.JsValueWrapper

  implicit val objectMapFormat = new Format[Map[String, Object]] {

    def writes(map: Map[String, Object]): JsValue =
      Json.obj(map.map{case (s, o) =>
        val ret:(String, JsValueWrapper) = o match {
          case _:String => s -> JsString(o.asInstanceOf[String])
          case _:Number => s -> JsNumber(o.asInstanceOf[Long])
          case t:Map[String,Object] => s -> writes(t)
          case None => s -> JsNull
          case _ => s -> JsArray(o.asInstanceOf[List[String]].map(JsString))
        }
        ret
      }.toSeq:_*)

    def reads(jv: JsValue): JsResult[Map[String, Object]] =
      JsSuccess(jv.as[Map[String, JsValue]].map{case (k, v) =>
        k -> (v match {
          case s:JsString => s.as[String]
//          case t:JsNumber => t.as[Long]
          case l => l.as[List[String]]
        })
      })
  }

  val rounds = GameDAO.gameRounds(game.id)

  def opAnswers = {
    if(game by user){
      rounds.map(_.map(r => Map(r.id.toString -> Map(
        r.quesOneId.toString -> r.utwoAnsOneId,
        r.quesTwoId.toString -> r.utwoAnsTwoId,
        r.quesThreeId.toString -> r.utwoAnsThreeId
        ))
      ))
    }else{
      rounds.map(_.map(r => Map(r.id.toString ->Map(
        r.quesOneId.toString -> r.uoneAnsOneId,
        r.quesTwoId.toString -> r.uoneAnsTwoId,
        r.quesThreeId.toString -> r.uoneAnsThreeId
        ))
      ))
    }
//    Future.successful(List("2" -> 3, "3"->5))
  }

  def submit(gameRound: GameRound): Unit = {
    if(game by user) RoundDAO.countAndSaveScores(gameRound.answers, game, left = true)
    else if(game opp user) RoundDAO.countAndSaveScores(gameRound.answers, game, left = true)
    rounds.flatMap(RoundDAO.submitRound(gameRound, game, user.id, _))
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
      opAns <- opAnswers
    } yield {
      Json.toJson(gameData).as[JsObject] +
        ("roundId" -> Json.toJson(round.id)) +
        ("data" -> Json.toJson(rData)) +
        ("opAnswers" -> Json.toJson(opAns))
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

