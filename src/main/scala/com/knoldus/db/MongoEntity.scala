package com.knoldus.db

import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.{FindObservable, MongoCollection, MongoDatabase}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.reflect.ClassTag

abstract class MongoEntity[T](implicit c: ClassTag[T]) {

  protected val database: MongoDatabase

  val collectionName: String

  def createMany(t: Seq[T]): Future[Seq[T]] = {

    collection.insertMany(t).toFuture().map(_ => t)
  }

  def create(t: T): Future[T] = {

    collection.insertOne(t).toFuture().map(_ => t)
  }

  def deleteOne(t: Int, fieldName: String): Future[Int] = {

    collection.deleteOne(equal(fieldName, t)).toFuture().map(_ => t)
  }

  def deleteMany(t: Seq[Int], fieldName: String): Seq[Future[Seq[Int]]] = {

    t.map(data => collection.deleteMany(equal(fieldName, data)).toFuture().map(_ => t))
  }

  def findOne(t: Int, fieldName: String): Future[T] = {

    collection.find(equal(fieldName, t)).first().toFuture()
  }

  def findAll: FindObservable[T] = {

    collection.find()
  }

  def update(existing: Int, t: Int, fieldName: String): Future[Int] = {
    collection.updateOne(equal(fieldName, existing), set(fieldName, t)).toFuture().map(_ => t)

  }

  def count(t: Int, fieldName: String): Future[Int] = {

    collection.count(equal(fieldName, t)).toFuture().map(_.toInt)

  }

  implicit def collection: MongoCollection[T] =
    database.getCollection(collectionName)

}
