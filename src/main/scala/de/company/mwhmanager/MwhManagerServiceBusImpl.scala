package de.company.mwhmanager

import de.company.{Heartbeater, Idler}
import de.company.bus.rabbitMQ.BusImpl
import de.company.bus._
import de.company.model.{ModelServiceClientBusImpl, ModelService}
import de.company.modelmanager.ModelManagerServiceClientBusImpl

import scala.concurrent.Future

class MwhManagerServiceBusImpl(bus: Bus, mwhManagerService: MwhManagerService, modelService: ModelService) extends MwhManagerService {
  import scala.concurrent.ExecutionContext.Implicits.global

  bus.registerReplyCallback(CommandSubjects.mwhManagerBase + Commands.MwhManagerCreateCmd, create)
  bus.registerReplyCallback(CommandSubjects.mwhManagerBase + Commands.MwhManagerStartImportCmd, startImportReply)

  bus.subscribe(EventSubjects.transBase + Events.TransformationStartedEvent, transformationStartedListener)
  bus.subscribe(EventSubjects.transBase + Events.TransformationDoneEvent, transformationDoneListener)

  bus.subscribe(EventSubjects.transBase + Events.ModelImportStartedEvent, modelImportStartedListener)
  bus.subscribe(EventSubjects.transBase + Events.ModelImportPart1DoneEvent, modelImportPart1DoneListener)
  bus.subscribe(EventSubjects.transBase + Events.ModelImportPart2DoneEvent, modelImportPart2DoneListener)
  bus.subscribe(EventSubjects.transBase + Events.ModelImportDoneEvent, modelImportDoneListener)

  def create(name: String): Future[String] = ???

  def startImport(blobStoreId: String) = mwhManagerService.startImport(blobStoreId)

  def transformationStartedListener(msg: String): Unit ={
    println(s"TransformationStarted")
  }

  def transformationDoneListener(blobStoreId: String): Unit ={
    println(s"TransformationDone : blobStoreId = '$blobStoreId'")
    startImport(blobStoreId)
  }

  def modelImportStartedListener(blobStoreId: String): Unit = {
    println(s"ModelImportStarted : blobStoreId = '$blobStoreId'")
  }

  def modelImportPart1DoneListener(blobStoreId: String): Unit = {
    println(s"ModelImportPart1Done : blobStoreId = '$blobStoreId'")
    modelService.switch()
  }

  def modelImportPart2DoneListener(blobStoreId: String): Unit = {
    println(s"ModelImportPart2Done : blobStoreId = '$blobStoreId'")
  }

  def modelImportDoneListener(blobStoreId: String): Unit = {
    println(s"ModelImportDone : blobStoreId = '$blobStoreId'")
  }

  def startImportReply(blobStoreId: String):Future[String] = startImport(blobStoreId) map (ret => ret.toString)
}


object MwhManagerServiceBusApp extends App with Idler with Heartbeater {
  val bus                       = new BusImpl
  val modelManagerServiceClient = new ModelManagerServiceClientBusImpl(bus)
  val modelServiceClient        = new ModelServiceClientBusImpl(bus)
  val mwhManagerService         = new MwhManagerServiceImpl(modelManagerServiceClient)
  val mwhManagerServiceBus      = new MwhManagerServiceBusImpl(bus, mwhManagerService, modelServiceClient)

  beat(bus, "MwhManagerServiceBusApp")

  idle(200)
}
