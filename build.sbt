name := "QuizScala"

version := "1.0"

lazy val quizscala = (project in file(".")).enablePlugins(PlayScala).dependsOn(webservice).dependsOn(admin)

lazy val admin = (project in file("modules/admin")).enablePlugins(PlayScala)
lazy val webservice = (project in file("modules/webservice")).enablePlugins(PlayScala).dependsOn(admin).aggregate(admin)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  cache , ws,
  "mysql" % "mysql-connector-java" % "5.1.25",
  "com.typesafe.slick" %% "slick" % "3.0.3",
  "com.typesafe.play" %% "play-slick" % "1.0.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.0.1",
  "org.joda" % "joda-money" % "0.9",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "org.mindrot" % "jbcrypt" % "0.3m"
)


libraryDependencies += specs2 % Test

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )


fork in run := true