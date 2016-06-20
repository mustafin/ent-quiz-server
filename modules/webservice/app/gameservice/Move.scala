package gameservice

import models.webservice.{GameRound, Round, GameUser}
import play.api.libs.json.JsValue

/**
 * Created by Murat.
 */
sealed trait Move {

  def isReply: Boolean
  def submit(): Unit
  def get(): JsValue
  def serialized(): JsValue

}

object Move{
//  def from(): Move = {
//
//  }
}

class MyMove(game: GameRound, user: GameUser) extends Move with GameService{

  override def isReply: Boolean = false

  override def submit(): Unit = submit(game, user)

  override def get(): JsValue = ???

  override def serialized(): JsValue = ???

}

class OpponentMove(game: GameRound, user: GameUser) extends Move with GameService{

  override def isReply: Boolean = true

  override def submit(): Unit = submit(game, user)

  override def get(): JsValue = ???

  override def serialized(): JsValue = ???

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

