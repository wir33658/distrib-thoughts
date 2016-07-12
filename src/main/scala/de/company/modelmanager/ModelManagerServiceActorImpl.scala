package de.company.modelmanager

import akka.actor.{Props, ActorSystem, Actor}
import com.typesafe.config.ConfigFactory
import de.company.blobstore.{BlobStoreServiceBusImpl, BlobStoreServiceImpl}
import de.company.bus.EventSubjects
import de.company.bus.rabbitMQ.BusImpl

case class ModelImportStart(blobStoreId: String)

class ModelManagerServiceActorImpl(modelManagerService: ModelManagerService) extends Actor {
  import scala.concurrent.ExecutionContext.Implicits.global

  def receive = {
    case ModelImportStart(blobStoreId: String) =>
      println("ModelManagerServiceActorImpl.ModelImportStart")
       modelManagerService.startImport(blobStoreId).map(sender ! _)

    case other =>
      println(s"ModelManagerServiceActorImpl : unknown message = '$other'")
  }
}

object ModelManagerServiceActorApp extends App {
  val config = ConfigFactory.load()
  val system = ActorSystem("actorsystemname", config.getConfig("modelmanagerservice"))

  val bus                     = new BusImpl
  val blobStoreService        = new BlobStoreServiceImpl
  val blobStoreServiceBus     = new BlobStoreServiceBusImpl(bus, blobStoreService)
  val modelManagerServiceBus  = new ModelManagerServiceBusImpl(bus, blobStoreServiceBus)

  val props = Props(classOf[ModelManagerServiceActorImpl], modelManagerServiceBus)
  val modelManagerActor = system.actorOf(props, "ModelManagerActor")

  println("ModelManagerServiceActorApp up.")
}