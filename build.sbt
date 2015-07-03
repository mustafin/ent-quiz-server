name := "QuizScala"

version := "1.0"

lazy val `quizscala` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  cache , ws,
  "mysql" % "mysql-connector-java" % "5.1.18",
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "com.typesafe.play" %% "play-slick" % "0.8.0",
  "org.joda" % "joda-money" % "0.9",
  "org.slf4j" % "slf4j-nop" % "1.6.4"
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  