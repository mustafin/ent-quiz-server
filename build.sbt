name := "QuizScala"

version := "1.0"

lazy val `quizscala` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.10.5"

libraryDependencies ++= Seq(
  jdbc , anorm , cache , ws,
  "org.sorm-framework" % "sorm" % "0.3.8"
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  