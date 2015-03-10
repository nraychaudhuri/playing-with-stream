import play.PlayImport.PlayKeys._
import play.twirl.sbt.Import.TwirlKeys._

name := "pagelets"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "com.typesafe.play" %% "play-streams-experimental" % "2.4-SNAPSHOT",
  "com.typesafe.akka" %% "akka-stream-experimental" % "1.0-M3"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

templateFormats := templateFormats.value + ("pagelet" -> "util.PageletFormat")

templateImports += "util.Pagelet"
