name := "akka-quickstart-scala"

version := "1.0"

scalaVersion := "2.12.2"

lazy val akkaVersion = "2.4.18"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.17",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)
