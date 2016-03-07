name := "rec-parse"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.7"

organization := "com.briefscala"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.18",
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "com.typesafe" % "config" % "1.3.0",
  "org.scalaz" %% "scalaz-core" % "7.1.7",
  "com.chuusai" %% "shapeless" % "2.3.0"
)

scalacOptions ++= Seq(
  "-unchecked",
  "-Xlint",
  "-deprecation",
  "-target:jvm-1.7",
  "-encoding", "UTF-8",
  "-Ywarn-dead-code",
  "-language:_",
  "-feature"
)

assemblyJarName in assembly := "rec-parse.jar"
