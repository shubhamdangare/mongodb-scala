package com.knoldus.dao

import com.knoldus.db.MongoEntity
import com.knoldus.domain.User
import org.mongodb.scala.MongoDatabase

class UserDao(mongodb: MongoDatabase) extends MongoEntity[User] {

  override protected val database: MongoDatabase = mongodb

  override val collectionName: String = "User"

}
