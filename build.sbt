name := "scala-mongodb"

version := "0.1"

scalaVersion := "2.12.8"

val LogbackVersion = "1.2.3"
val MongoDBScalaDriverVersion = "2.5.0"

lazy val root = (project in file("."))
  .settings(
    name := "root",
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "org.mongodb.scala" %% "mongo-scala-driver" % MongoDBScalaDriverVersion,
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
      "com.typesafe" % "config" % "1.3.3",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
      "log4j" % "log4j" % "1.2.17",
      "org.scalatest" %% "scalatest" % "3.0.5" % "test",
      "org.mockito" % "mockito-core" % "2.23.0",
      "org.mockito" %% "mockito-scala" % "1.1.4",
      "org.typelevel" %% "cats-core" % "1.6.1"
    )
  )
