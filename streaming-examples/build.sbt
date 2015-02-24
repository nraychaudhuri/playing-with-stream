name := """streaming-examples"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,  
  "com.typesafe.play" %% "play-streams-experimental" % "2.4-SNAPSHOT",
  "com.typesafe.akka" %% "akka-stream-experimental" % "1.0-M2"
)
