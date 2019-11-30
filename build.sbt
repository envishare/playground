name := "sangria-playground"
description := "An example of GraphQL server written with Play and Sangria."

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.6"

resolvers ++= Seq(
  "Maven Repository" at "https://mvnrepository.com/artifact/"
)

libraryDependencies ++= Seq(
  guice,
  filters,
  "com.typesafe.play" %% "play-ahc-ws" % "2.6.1",
  "org.sangria-graphql" %% "sangria" % "1.4.2",
  "org.sangria-graphql" %% "sangria-slowlog" % "0.1.8",
  "org.sangria-graphql" %% "sangria-play-json" % "1.0.4",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "org.elasticsearch.client" % "transport" % "6.2.3",
  "org.apache.commons" % "commons-lang3" % "3.9"
)

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayScala)
enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)