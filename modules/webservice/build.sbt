name := "mywebservice"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "5.1.25",
  "com.typesafe.slick" %% "slick" % "3.0.3",
  "com.typesafe.play" %% "play-slick" % "1.0.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.0.1",
  "org.joda" % "joda-money" % "0.9",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "io.really" %% "jwt-scala" % "1.2.2"
)