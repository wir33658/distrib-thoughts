package de.company.blobstore

import akka.actor.{Props, ActorSystem, Actor}
import com.typesafe.config.ConfigFactory
import de.company.bus.EventSubjects
import de.company.bus.rabbitMQ.BusImpl

case class Store(data: String)
case class Get(id: String)

class BlobStoreServiceActorImpl(blobStoreService: BlobStoreService) extends Actor {
  import scala.concurrent.ExecutionContext.Implicits.global

  def receive = {
    case Store(data: String) =>
      println("BlobStoreServiceActorImpl.Store")
      blobStoreService.store(data).map(sender ! _)

    case Get(id: String) =>
      println("BlobStoreServiceActorImpl.Get")
      blobStoreService.get(id).map(sender ! _)

    case other =>
      println(s"BlobStoreServiceActorImpl : unknown message = '$other'")
  }
}

object BlobStoreServiceActorApp extends App {
  val bus                 = new BusImpl
  val blobStoreService    = new BlobStoreServiceImpl
  val blobStoreServiceBus = new BlobStoreServiceBusImpl(bus, blobStoreService)

  val config          = ConfigFactory.load()
  val system          = ActorSystem("actorsystemname", config.getConfig("blobstoreservice"))
  val props           = Props(classOf[BlobStoreServiceActorImpl], blobStoreServiceBus)
  val blobStoreActor  = system.actorOf(props, "BlobStoreActor")

  println("BlobStoreServiceActorApp up.")
}