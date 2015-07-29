name := "akkaweather"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.1",
  "org.json4s" %% "json4s-jackson" % "3.2.10",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.2"
)

