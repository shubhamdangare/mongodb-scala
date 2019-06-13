package com.knoldus.db

import com.knoldus.service.UserService
import com.knoldus.utils.ThrowableExtensions._
import com.typesafe.config.ConfigFactory
import org.apache.log4j.Logger
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.{MongoClient, MongoDatabase}
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success, Try}

object MongoConnection extends App {

  val conf = ConfigFactory.load()
  implicit val logger = Logger.getLogger(this.getClass)
  val mongoClient = MongoClient(conf.getString("mongo.url"))
  val mongoDatabase = mongoClient.getDatabase(conf.getString("mongo.db-name"))
  tryConnectToMongo(mongoDatabase)

  val userService = new UserService(mongoClient, conf)

  private def tryConnectToMongo(mongoDatabase: MongoDatabase) =
    Try(Await.result(mongoDatabase.runCommand(Document("ping" -> 1)).toFuture, 30.seconds)) match {
      case Failure(exception) =>
        logger.error(s"Could not connect to Mongo on bootstrap. Error: ${exception.printInfo}")
        throw exception
      case Success(value) =>
        value
    }


}
