package com.knoldus.domain

import org.mongodb.scala.bson.ObjectId

object User {
  def apply(firstName: String,
            lastName: String,
            country: String,
            dob: String,
            mobileNumber: Int,
            address: String
           ): User =
    User(new ObjectId(), firstName, lastName, country, dob, mobileNumber, address)
}

case class User(
                 _id: ObjectId,
                 firstName: String,
                 lastName: String,
                 country: String,
                 dob: String,
                 mobileNumber: Int,
                 address: String
               )