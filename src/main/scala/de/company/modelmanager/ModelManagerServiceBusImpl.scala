package de.company.modelmanager

import de.company.{Heartbeater, Idler}
import de.company.blobstore.{BlobStoreServiceClientBusImpl, BlobStoreService}

import scala.concurrent.Future

import de.company.bus.rabbitMQ.BusImpl

import de.company.bus._

class ModelManagerServiceBusImpl(bus: Bus, blobStoreService: BlobStoreService) extends ModelManagerServiceImpl(blobStoreService) {
  import scala.concurrent.ExecutionContext.Implicits.global

  bus.reply(CommandSubjects.modelManagerBase + Commands.ModelManagerStartImportCmd, startImportReply)

  override def startImport(blobStoreId: String): Future[Boolean] = {
    bus.publish(EventSubjects.transBase + Events.ModelImportStartedEvent, blobStoreId)
    super.startImport(blobStoreId)
  }

  override def startImport1(blobStoreId: String, dataToImport: String): Unit ={
    super.startImport1(blobStoreId, dataToImport)
    bus.publish(EventSubjects.transBase + Events.ModelImportPart1DoneEvent, blobStoreId)
  }

  override def startImport2(blobStoreId: String, dataToImport: String): Unit ={
    super.startImport2(blobStoreId, dataToImport)
    bus.publish(EventSubjects.transBase + Events.ModelImportPart2DoneEvent, blobStoreId)
  }

  override def importDone(blobStoreId: String): Unit ={
    super.importDone(blobStoreId)
    bus.publish(EventSubjects.transBase + Events.ModelImportDoneEvent, blobStoreId)
  }

  def startImportReply(blobStoreId: String):Future[String] = startImport(blobStoreId) map (ret => ret.toString)
}


object ModelManagerServiceBusApp extends App with Idler with Heartbeater {
  val bus                     = new BusImpl
  val blobStoreServiceClient  = new BlobStoreServiceClientBusImpl(bus)
  val modelManagerService     = new ModelManagerServiceBusImpl(bus, blobStoreServiceClient)

  beat(bus, "ModelManagerServiceBusApp")

  idle(200)
}
