name := """rabbitmq-akka-stream"""

version := "2.0"

organization := "io.scalac"

scalaVersion := "2.11.6"

resolvers ++= Seq(
  "snapshots"           at "http://oss.sonatype.org/content/repositories/snapshots",
  "releases"            at "http://oss.sonatype.org/content/repositories/releases",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation", "-encoding", "utf8")

//doesn't work with Activator
//EclipseKeys.withSource := true

libraryDependencies ++= {
  Seq(
    "com.typesafe.akka"          %%  "akka-actor"               % "2.3.9",
    "com.typesafe.akka"          %%  "akka-stream-experimental" % "1.0-M5",
    "com.typesafe.akka"	         %%  "akka-http-experimental"   % "1.0-M5",
    "com.typesafe.akka"	         %%  "akka-http-core-experimental"   % "1.0-M5",
    "io.scalac"                  %%  "reactive-rabbit"          % "0.2.2",
    "com.typesafe.scala-logging" %%  "scala-logging-slf4j"      % "2.1.2",
    "ch.qos.logback"             %   "logback-core"             % "1.1.2",
    "ch.qos.logback"             %   "logback-classic"          % "1.1.2",
    "org.scalatest"              %%  "scalatest"                % "2.2.4" % "test"
  )
}
