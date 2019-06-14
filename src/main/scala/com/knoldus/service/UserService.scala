package com.knoldus.service

import java.util.concurrent.TimeUnit

import cats.implicits._
import com.knoldus.dao.UserDao
import com.knoldus.domain.User
import com.knoldus.service.UserService.UserServiceError
import com.typesafe.config.Config
import org.apache.log4j.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class UserService(dao: UserDao, conf: Config)(implicit val logger: Logger) {


  def addUser(user: User): Future[Either[UserServiceError, User]] = {

    if (getCount(user.mobileNumber) == 0) {
      logger.info("user added successfully")
      for {user <- dao.create(user)} yield user
      Future.successful(user.asRight)
    }
    else {
      logger.info("user already exists")
      Future.successful(UserServiceError.UserAlreadyExist.asLeft)
    }

  }

  def deleteUser(userMobile: Int): Future[Either[UserServiceError, Int]] = {

    if (getCount(userMobile) == 1) {
      val result = for {
        user <- dao.findOne(userMobile, "mobileNumber")
        data <- dao.deleteOne(user.mobileNumber, "mobileNumber")
      } yield data
      result.map(value => value.asRight)
    }
    else {
      logger.info("user not found")
      Future.successful(UserServiceError.UserNotFound.asLeft)
    }

  }

  def updateUser(user: User, mobile: Int): Future[Either[UserServiceError, Int]] = {

    if (getCount(user.mobileNumber) == 1) {
      val result = for {
        user <- dao.findOne(user.mobileNumber, "mobileNumber")
        data <- dao.update(user.mobileNumber, mobile, "mobileNumber")
      } yield data
      result.map(value => value.asRight)
    }
    else {
      logger.info("user not found")
      Future.successful(UserServiceError.UserNotFound.asLeft)
    }

  }

  def findUser(number: Int): Future[Either[UserServiceError, User]] = {

    if (getCount(number) == 1) {
      logger.info("user added successfully")
      dao.findOne(number, "mobileNumber").map(_.asRight)
    }
    else {
      logger.info("user already exists")
      Future.successful(UserServiceError.UserNotFound.asLeft)
    }

  }

  def getCount(mobileNumber: Int): Int = {

    Await.result(dao.count(mobileNumber, "mobileNumber"), Duration(5, TimeUnit.SECONDS))
  }

}

object UserService {

  sealed trait UserServiceError

  object UserServiceError {

    case object UserNotFound extends UserServiceError

    case object UserNotUpdatedDenied extends UserServiceError

    case object UserNotDeleted extends UserServiceError

    case object UserAlreadyExist extends UserServiceError

  }

}
