name := "mywebservice"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "5.1.25",
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "com.typesafe.play" %% "play-slick" % "0.8.0",
  "org.joda" % "joda-money" % "0.9",
  "org.slf4j" % "slf4j-nop" % "1.6.4"
)