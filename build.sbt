name := "akka-quickstart-scala"

version := "1.0"

scalaVersion := "2.12.2"

lazy val akkaVersion = "2.4.18"
lazy val playVersion = "2.6"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.lightbend.akka" %% "akka-stream-alpakka-amqp" % "0.9",
  "com.typesafe.play" % "play-json_2.12" % "2.6.3",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)
