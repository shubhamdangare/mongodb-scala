package com.knoldus.db

import java.util.concurrent.TimeUnit
import com.knoldus.dao.UserDao
import com.knoldus.domain.User
import com.knoldus.service.UserService
import com.knoldus.utils.ThrowableExtensions._
import com.typesafe.config.ConfigFactory
import org.apache.log4j.Logger
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.collection.mutable.Document
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, DurationInt}
import scala.util.{Failure, Success, Try}

object MongoApplication extends App {

  val conf = ConfigFactory.load()
  implicit val logger = Logger.getLogger(this.getClass)
  val mongoClient = MongoClient(conf.getString("mongo.url"))
  val codecRegistry = fromRegistries(fromProviders(classOf[User]), DEFAULT_CODEC_REGISTRY)
  val mongoDatabase: MongoDatabase = mongoClient.getDatabase(conf.getString("mongo.db-name")).withCodecRegistry(codecRegistry)
  val collection: MongoCollection[User] = mongoDatabase.getCollection(conf.getString("mongo.collection-name"))

  tryConnectToMongo(mongoDatabase)

  val dao = new UserDao(mongoDatabase)
  val userService = new UserService(dao, conf)

  val person: User = User("Ada", "Lovelace", "us", "17/1970", 55522222, "WD")

  // Await.result(userService.AddUser(person), Duration(10, TimeUnit.SECONDS))
  //userService.updateUser(person,111311111)

  Await.result(userService.findUser(111311111), Duration(10, TimeUnit.SECONDS))

  def tryConnectToMongo(mongoDatabase: MongoDatabase) =
    Try(Await.result(mongoDatabase.runCommand(Document("ping" -> 1)).toFuture, 30.seconds)) match {
      case Failure(exception) =>
        logger.error(s"Could not connect to Mongo on bootstrap. Error: ${exception.printInfo}")
        throw exception
      case Success(value) =>
        value
    }


}
