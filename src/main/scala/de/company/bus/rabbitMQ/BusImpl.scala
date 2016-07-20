package de.company.bus.rabbitMQ

import com.rabbitmq.client.AMQP.BasicProperties.Builder
import com.rabbitmq.client._
import de.company.bus.{ReqRep, PubSub, Bus}

import scala.concurrent.Future

trait RabbitMqBasics {
  val factory = new ConnectionFactory()
  factory.setHost("localhost")
  val connection = factory.newConnection()

}

trait PubSubImpl extends RabbitMqBasics with PubSub {
  val EXCHANGE_NAME = "topic_pub_sub"
  val publishChannel = connection.createChannel()

  def subscribe(subject: String, callback: String => Unit) = {
    println(s"subscribe : on '$subject'")
    val subscription = new Subscription(connection, subject, callback)
  }

  def publish(subject: String, msg: String) = {
    // println(s"publish : '$msg' on '$subject'")
//    publishChannel.exchangeDeclare(subject, "fanout")
    publishChannel.exchangeDeclare(EXCHANGE_NAME, "topic")
//    publishChannel.basicPublish(subject, "", null, msg.getBytes)
    publishChannel.basicPublish(EXCHANGE_NAME, subject, null, msg.getBytes)
  }

  class Subscription(connection: Connection, subject: String, callback: String => Unit) {
    val channel = connection.createChannel()
//    channel.exchangeDeclare(subject, "fanout")
    channel.exchangeDeclare(EXCHANGE_NAME, "topic")
    val queueName = channel.queueDeclare.getQueue
//    channel.queueBind(queueName, subject, "")
    channel.queueBind(queueName, EXCHANGE_NAME, subject)

    val consumer = new DefaultConsumer(channel) {
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties, body: Array[Byte]): Unit = {
        // println(s"received event : consumerTag = '$consumerTag', envelope = '$envelope', properties = '$properties', body = '$body'")
        val msg = new String(body)
        callback(msg)
      }
    }
    channel.basicConsume(queueName, true, consumer)
  }
}

trait ReqRepImpl extends RabbitMqBasics with ReqRep {
  import scala.concurrent.ExecutionContext.Implicits.global
  import java.util.concurrent.ConcurrentHashMap
  import scala.collection.JavaConverters._

  val replyListeners = new ConcurrentHashMap[String, Listener] asScala
  val caller = new Caller(connection)
  val listenerChannel = connection.createChannel()

  def request(subject: String, msg: String):Future[String] = {
    caller.call(subject, msg)
  }

  def registerReplyCallback(subject: String, callback: String => Future[String]): Unit = {
    if(!replyListeners.contains(subject)){
//      val newListener = new Listener(connection, subject, callback)
      val newListener = new Listener(subject, callback)
      replyListeners.put(subject, newListener)
    }
  }

  class Caller(connection: Connection){
    val channel = connection.createChannel()
    val replyQueueName = channel.queueDeclare.getQueue
    val consumer = new QueueingConsumer(channel)
    channel.basicConsume(replyQueueName, true, consumer)
    val corrId = java.util.UUID.randomUUID().toString
    println("Caller created.")

    def call(subject: String, msg: String):Future[String]= Future {
      val props = new Builder().correlationId(corrId).replyTo(replyQueueName).build()
      channel.basicPublish("", subject, props, msg.getBytes)
      var response: String = ""
      var cont = true
      while(cont){
        val delivery = consumer.nextDelivery()
        if(delivery.getProperties.getCorrelationId.equals(corrId)){
          response = new String(delivery.getBody)
          cont = false
        }
      }
      response
    }
  }

  class Listener(subject: String, callback: String => Future[String]) {
//  class Listener(connection: Connection, subject: String, callback: String => Future[String]) {
    val listenerChannel = connection.createChannel()
    listenerChannel.queueDeclare(subject, false, false, false, null)
    listenerChannel.basicQos(1)

    val consumer = new QueueingConsumer(listenerChannel)
    listenerChannel.basicConsume(subject, false, consumer)
    println(s"Listener for '$subject' created.")

    Future {
      while (true) {
        val delivery = consumer.nextDelivery()
        val props = delivery.getProperties
        val corId = props.getCorrelationId
        val replyTo = props.getReplyTo
        val message = new String(delivery.getBody)
        callback(message) map { reply =>
          val replyProps = new Builder().correlationId(corId).build()
          listenerChannel.basicPublish("", replyTo, replyProps, reply.getBytes)
          listenerChannel.basicAck(delivery.getEnvelope.getDeliveryTag, false)
        }
      }
    }
  }

}

class BusImpl extends Bus with PubSubImpl with ReqRepImpl
