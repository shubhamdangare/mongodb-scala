name := "scala-mongodb"

version := "0.1"

scalaVersion := "2.12.4"

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
      "log4j" % "log4j" % "1.2.17"
    )
  )