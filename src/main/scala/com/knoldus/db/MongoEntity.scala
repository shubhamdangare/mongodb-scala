package com.knoldus.db

import com.knoldus.db.Helpers._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.{Completed, FindObservable, MongoCollection, SingleObservable}
import scala.reflect.ClassTag


abstract class MongoEntity[T](implicit c: ClassTag[T]) {

  def createMany(t: Seq[T], collection: MongoCollection[T]): Seq[Completed] = {

    collection.insertMany(t).results()
  }

  def create(t: T, collection: MongoCollection[T]): Seq[Completed] = {

    collection.insertOne(t).results()
  }

  def deleteOne(t: Int, collection: MongoCollection[T], fieldName: String): Unit = {

    collection.deleteOne(equal(fieldName, t)).printHeadResult("deleted")
  }

  def deleteMany(t: Seq[Int], collection: MongoCollection[T], fieldName: String): Unit = {

    t.map(data => collection.deleteMany(equal(fieldName, data)))
  }


  def findOne(t: Int, collection: MongoCollection[T], fieldName: String): SingleObservable[T] = {

    collection.find(equal(fieldName, t)).first()
  }

  def findAll(collection: MongoCollection[T]): FindObservable[T] = {

    collection.find()
  }

  def update(existing: Int, t: Int, collection: MongoCollection[T], fieldName: String) = {
    collection.updateOne(equal(fieldName, existing), set(fieldName, t)).printHeadResult("Update Result: ")

  }

  def count(t: Int, collection: MongoCollection[T], fieldName: String): SingleObservable[Long] = {

    collection.count(equal(fieldName, t))

  }

}
