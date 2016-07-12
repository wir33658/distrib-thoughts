package de.mid.trans

import akka.actor.{Props, ActorSystem, Actor}
import com.typesafe.config.ConfigFactory
//import de.mid.blobstore.{BlobStoreServiceImpl, BlobStoreServiceBusImpl, BlobStoreService, BlobStoreServiceClientActorImpl}
import de.mid.blobstore.BlobStoreServiceClientActorImpl
import de.mid.bus.{Events, EventSubjects}
import de.mid.bus.rabbitMQ.BusImpl

case class TransStart(rdf: String)
case class Status()

class TransServiceActorImpl(transService: TransService) extends Actor {
  import scala.concurrent.ExecutionContext.Implicits.global

  def receive = {
    case TransStart(rdf: String) =>
      println("TransServiceActorImpl.TransStart :")
      val f = transService.start(rdf)
      f.map(sender ! _)
      println("TransServiceActorImpl.TransStart : done")
    case other => println(s"TransServiceActorImpl : unknown message = '$other'")
  }
}

object TransServiceActorApp extends App {
  val config = ConfigFactory.load()
  val system = ActorSystem("smartfacts", config.getConfig("transservice"))

  val blobStoreClientActor = new BlobStoreServiceClientActorImpl(Some(system))

  val bus                 = new BusImpl
//  val blobStoreService    = new BlobStoreServiceImpl
//  val blobStoreServiceBus = new BlobStoreServiceBusImpl(bus, blobStoreService)

  val transService        = new TransServiceImpl(blobStoreClientActor)
  val transServiceBus     = new TransServiceBusImpl(bus, transService)

  val props = Props(classOf[TransServiceActorImpl], transServiceBus)
  val transActor = system.actorOf(props, "TransActor")

  bus.subscribe(EventSubjects.transBase + Events.StartTransformationTriggerEvent, startTransformationTriggerEventListener)

  println("TransServiceActorApp up.")

  // Only a helper for demo purposes.
  def startTransformationTriggerEventListener(msg: String): Unit ={
    transActor ! TransStart(msg)
  }
}