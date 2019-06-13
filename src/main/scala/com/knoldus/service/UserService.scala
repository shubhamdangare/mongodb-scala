package com.knoldus.service

import java.util.concurrent.TimeUnit

import com.knoldus.db.MongoEntity
import com.knoldus.domain.User
import com.knoldus.db.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import com.typesafe.config.Config
import org.apache.log4j.Logger
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.{MongoClient, MongoCollection}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.util.Success

class UserService(mongoClient: MongoClient, conf: Config)(implicit val logger: Logger) extends MongoEntity[User] {

  val codecRegistry = fromRegistries(fromProviders(classOf[User]), DEFAULT_CODEC_REGISTRY)
  val mongoDatabase = mongoClient.getDatabase(conf.getString("mongo.db-name")).withCodecRegistry(codecRegistry)
  val collection: MongoCollection[User] = mongoDatabase.getCollection(conf.getString("mongo.collection-name"))

  val person: User = User("Ada", "Lovelace", "us", "17/1970", 222222222, "WD")

  def AddUser(user: User): Future[Unit] = Future {

    if (getCount(user.mobileNumber) == 0) {
      super.create(user, collection)
      logger.info("user added successfully")
    }
    else {
      logger.info("user already exists")
    }

  }

  def deleteUser(user: User): Future[Unit] = Future {

    if (getCount(user.mobileNumber) == 1) {
      super.deleteOne(user.mobileNumber, collection, "mobileNumber")
      logger.info("user deleted Successfully")
    }
    else {
      logger.info("user not found")
    }

  }

  def updateUser(user: User, mobileNumber: Int): Future[Unit] = Future {

    if (getCount(user.mobileNumber) == 1) {
      val data = super.findOne(user.mobileNumber, collection, "mobileNumber")
      data.toFuture().onComplete(user => user match {
        case Success(value) =>
          super.update(value.mobileNumber, mobileNumber, collection, "mobileNumber")
        case _ => throw new RuntimeException("unable to update user")
      })
      logger.info("user deleted Successfully")
    }
    else {
      logger.info("user not found")
    }

  }

  def findUser(number: Int): Future[Unit] = Future {

    if (getCount(number) == 1) {
      super.findOne(number, collection, "mobileNumber").printHeadResult("User Data : ---")
      logger.info("user added successfully")
    }
    else {
      logger.info("user already exists")
    }

  }

  private def getCount(mobileNumber: Int): Long =
    Await.result(super.count(mobileNumber, collection, "mobileNumber").toFuture(), Duration(10, TimeUnit.SECONDS))

}
