package com.knoldus.service


import com.knoldus.dao.UserDao
import com.knoldus.domain.User
import com.knoldus.service.UserService.UserServiceError._
import com.typesafe.config.ConfigFactory
import org.apache.log4j.Logger
import org.mockito.Mockito.when
import scala.concurrent.ExecutionContext.Implicits.global
import org.mongodb.scala.MongoDatabase
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{Matchers, WordSpec}
import cats._
import implicits._

import scala.concurrent.Future
import scala.util.Success

class UserServiceSpec extends WordSpec with ScalaFutures with Matchers with MockitoSugar with TableDrivenPropertyChecks {

  val conf = ConfigFactory.load()
  implicit val logger = Logger.getLogger(this.getClass)
  protected val dbLayer: MongoDatabase = mock[MongoDatabase]
  protected val dao: UserDao = mock[UserDao]
  val userService = new UserService(dao, conf)
  val person: User = User("Ada", "Lovelace", "us", "17/1970", 222222222, "WD")

  def future[A](a: A): Future[A] = Future.successful(a)

  "UserService#addUser" should {
    "add user successfully" in {
      when(
        dao.count(person.mobileNumber, "mobileNumber")
      ).thenReturn(future(0))
      when(
        dao.create(person)
      ).thenReturn(future(person))

      whenReady(userService.AddUser(person))(_ shouldBe person.asRight)
    }


    "unable to add user if user already exist" in {
      when(
        dao.count(person.mobileNumber, "mobileNumber")
      ).thenReturn(future(1))
      whenReady(userService.AddUser(person))(_ shouldBe UserAlreadyExist.asLeft)
    }
  }

  "UserService#deleteUser" should {
    "delete user successfully" in {
      when(
        dao.count(person.mobileNumber, "mobileNumber")
      ).thenReturn(future(1))

      when(
        dao.findOne(person.mobileNumber, "mobileNumber")
      ).thenReturn(future(person))
      when(
        dao.deleteOne(person.mobileNumber, "mobileNumber")
      ).thenReturn(future(1))
      whenReady(userService.deleteUser(person.mobileNumber))(_ shouldBe 1.asRight)
    }

    "unable to delete user if user already exist" in {
      when(
        dao.count(person.mobileNumber, "mobileNumber")
      ).thenReturn(future(0))

      when(
        dao.findOne(person.mobileNumber, "mobileNumber")
      ).thenReturn(future(person))
      whenReady(userService.deleteUser(person.mobileNumber))(_ shouldBe UserNotFound.asLeft)
    }
  }

  "UserService#findUSer" should {
    "user found successfully" in {
      when(
        dao.count(person.mobileNumber, "mobileNumber")
      ).thenReturn(future(1))
      when(
        dao.findOne(person.mobileNumber, "mobileNumber")
      ).thenReturn(future(person))

      whenReady(userService.findUser(person.mobileNumber))(_ shouldBe person.asRight)
    }

    "unable to find user if user not already exist" in {
      when(
        dao.count(person.mobileNumber, "mobileNumber")
      ).thenReturn(future(0))

      whenReady(userService.findUser(person.mobileNumber))(_ shouldBe UserNotFound.asLeft)
    }
  }

  "UserService#updateUSer" should {
    "update user successfully" in {
      when(
        dao.count(person.mobileNumber, "mobileNumber")
      ).thenReturn(future(1))
      when(
        dao.findOne(person.mobileNumber, "mobileNumber")
      ).thenReturn(future(person))
      when(
        dao.update(person.mobileNumber, 11111111, "mobileNumber")
      ).thenReturn(future(1))

      whenReady(userService.updateUser(person, 11111111))(_ shouldBe 1.asRight)
    }

    "unable to update user" in {
      when(
        dao.count(person.mobileNumber, "mobileNumber")
      ).thenReturn(future(0))

      whenReady(userService.updateUser(person, 11111111))(_ shouldBe UserNotFound.asLeft)
    }
  }

}
