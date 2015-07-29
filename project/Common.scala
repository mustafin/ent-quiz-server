import sbt._
import Keys._
import play.PlayImport._
import com.typesafe.sbt.web.SbtWeb.autoImport.{Assets, pipelineStages}

object Common{

  def appName = "quiz-scala"

  def settings(theName: String) = Seq(
    name := theName,
    organization := "com.murat",
    version := "1.0 SNAPSHOT",
    scalaVersion := "2.11.1"
  )

  val appSettings = settings(appName)

  val commonDependencies = Seq(
    cache , ws,
    "mysql" % "mysql-connector-java" % "5.1.25",
    "com.typesafe.slick" %% "slick" % "2.1.0",
    "com.typesafe.play" %% "play-slick" % "0.8.0",
    "org.joda" % "joda-money" % "0.9",
    "org.slf4j" % "slf4j-nop" % "1.6.4"
  )


}