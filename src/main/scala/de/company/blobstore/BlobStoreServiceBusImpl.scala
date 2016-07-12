package de.company.blobstore

import de.company.{Heartbeater, Idler}
import de.company.bus.rabbitMQ.BusImpl
import de.company.bus._

import scala.concurrent.Future

class BlobStoreServiceBusImpl(bus: Bus, blobStoreService: BlobStoreService) extends BlobStoreService {
  import scala.concurrent.ExecutionContext.Implicits.global

  bus.reply(CommandSubjects.blobStoreBase + Commands.BlobStoreStoreCmd, store)
  bus.reply(CommandSubjects.blobStoreBase + Commands.BlobStoreGetCmd, get)

  def store(data: String):Future[String] = {
    bus.publish(EventSubjects.blobStoreBase + Events.BlobStoreStartedEvent, Events.BlobStoreStartedEvent)
    blobStoreService.store(data) map { ret =>
      bus.publish(EventSubjects.blobStoreBase + Events.BlobStoreDoneEvent, Events.BlobStoreDoneEvent)
      ret
    }
  }

  def get(id:String):Future[String] = {
    println(s"BlobStoreServiceBusImpl.get : id = '$id'")
    blobStoreService.get(id)
  }
}

object BlobStoreServiceBusApp extends App with Idler with Heartbeater {
  val bus                 = new BusImpl
  val blobStoreService    = new BlobStoreServiceImpl
  val blobStoreServiceBus = new BlobStoreServiceBusImpl(bus, blobStoreService)

  beat(bus, "BlobStoreServiceBusApp")

  idle(200)
}

