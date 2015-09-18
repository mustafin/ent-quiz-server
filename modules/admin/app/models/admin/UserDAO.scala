package models.admin

import java.security.MessageDigest

import play.api.{Play, Logger}
import play.api.Play.current
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.slick.driver.MySQLDriver.simple._

/**
 * Created by Murat.
 */
object UserDAO{

  val db = DatabaseConfigProvider.get[JdbcProfile](Play.current).db

  def findByName(username: String): Option[User] = db withSession{ implicit  session =>
    Tables.users.filter(_.username === username).firstOption
  }

  def create(user: User) = db withSession{ implicit session =>
    val userToInsert = user.copy(password = encryptPassword(user.password))
    (Tables.users += userToInsert).run
  }
  
  def checkCredentials(username: String, password: String): Boolean = db withSession {
    implicit session =>
      val encrypted = encryptPassword(password)
      Tables.users.filter(x => x.username === username && x.password === encrypted).exists.run
  }

  /**
   * Password Hashing Using Message Digest Algo
   */
  def encryptPassword(password: String): String = {
    val algorithm: MessageDigest = MessageDigest.getInstance("SHA-256")
    val defaultBytes: Array[Byte] = password.getBytes
    algorithm.reset()
    algorithm.update(defaultBytes)
    val messageDigest: Array[Byte] = algorithm.digest
    getHexString(messageDigest)
  }

  /**
   * Generate HexString For Password & userId Encryption
   */
  def getHexString(messageDigest: Array[Byte]): String = {
    val hexString: StringBuffer = new StringBuffer
    messageDigest foreach { digest =>
      val hex = Integer.toHexString(0xFF & digest)
      if (hex.length == 1) hexString.append('0') else hexString.append(hex)
    }
    Logger.info("encrypt Data" + hexString.toString)
    hexString.toString
  }

}
