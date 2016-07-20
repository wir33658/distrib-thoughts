package de.company.trans

import java.util.concurrent.{TimeUnit, Executors}

import de.company.{Heartbeater, Idler}
import de.company.blobstore.BlobStoreServiceClientBusImpl
import de.company.bus._
import de.company.bus.rabbitMQ.BusImpl

import scala.concurrent.Future

class TransServiceBusImpl(bus: Bus, transService: TransService) extends TransService {
  import scala.concurrent.ExecutionContext.Implicits.global

  bus.registerReplyCallback(CommandSubjects.transBase + Commands.TransStartCmd, start)
  bus.registerReplyCallback(CommandSubjects.transBase + Commands.TransStatusCmd, statusReply)

  def start(rdf: String):Future[String] = {
    bus.publish(EventSubjects.transBase + Events.TransformationStartedEvent, rdf)
    transService.start(rdf) map {blobStoreId =>
      bus.publish(EventSubjects.transBase + Events.TransformationDoneEvent, blobStoreId)
      blobStoreId
    }
  }

  def status():Future[String] = ???

  def statusReply(dummy: String): Future[String] = status()
}

object TransServiceBusApp extends App with Idler with Heartbeater {
  val bus               = new BusImpl
  val blobStoreServiceClient  = new BlobStoreServiceClientBusImpl(bus)
  val transService      = new TransServiceImpl(blobStoreServiceClient)
  val transServiceBus   = new TransServiceBusImpl(bus, transService)

  beat(bus, "TransServiceBusApp")

  bus.subscribe(EventSubjects.transBase + Events.StartTransformationTriggerEvent, startTransformationTriggerEventListener)

  println("TransServiceApp up.")

  idle(200)
  // Only a helper for demo purposes.
  def startTransformationTriggerEventListener(msg: String): Unit ={
    transServiceBus.start(msg)
  }
}
