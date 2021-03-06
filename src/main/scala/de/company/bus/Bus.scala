package de.company.bus

import scala.concurrent.Future

trait PubSub {
  def subscribe(subject: String, callback: String => Unit)
  def publish(subject: String, msg: String)
}

trait ReqRep {
  def request(subject: String, msg: String):Future[String]
  def registerReplyCallback(subject: String, callback: String => Future[String])
}

trait Bus extends PubSub with ReqRep
