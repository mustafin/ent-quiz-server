package helpers

import javapns.notification.{PushNotificationPayload, PushedNotification}

import models.webservice.GameUser
import play.api.Play
import play.api.Play.current

/**
 * Created by Murat.
 */
object Push {
  import scala.collection.JavaConversions._
  import scala.collection.JavaConverters._

  val keystorePassword = ""

  def devPush(pushAlertMessage: String, devices: Seq[String], custom: Map[String, String] = Map(), badgeNumber: Int = 1): List[PushedNotification] = {

    val keystoreFile = Play.classloader.getResourceAsStream("cert/cert.p12")

    //Create payload
    val payload = PushNotificationPayload.complex()
    payload.addBadge(badgeNumber)
    payload.addAlert(pushAlertMessage)
    payload.addSound("default")
    if(custom.nonEmpty){
      for((k, v) <- custom){
        payload.addCustomDictionary(k, v)
      }
    }


//    val notifications:List[PushedNotification] = javapns.Push.payload(payload, keystoreFile, keystorePassword, false, devices).toList
    val results = javapns.Push.payload(payload, keystoreFile, keystorePassword, false, devices.asJava)

    for(notification <- results.getFailedNotifications.toList){
      println(notification.toString)
    }

    results.toList
  }

  def sendInvite(user: GameUser, devices: Seq[String]): List[PushedNotification] = {
    val custom = Map("type" -> "invite", "userId" -> user.id.get.toString)
    devPush(s"${user.username} invites you", devices, custom)
  }

  def rejectGame(user: GameUser, devices: Seq[String]): List[PushedNotification] = {
    val custom = Map("type" -> "reject", "userId" -> user.id.get.toString)
    devPush(s"${user.username} rejected game", devices, custom)
  }

}