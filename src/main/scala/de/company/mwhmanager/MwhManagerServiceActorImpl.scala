package de.company.mwhmanager

import akka.actor.{Props, ActorSystem, Actor}
import com.typesafe.config.ConfigFactory
import de.company.bus.EventSubjects
import de.company.bus.rabbitMQ.BusImpl
import de.company.model.ModelServiceClientActorImpl
import de.company.modelmanager.ModelManagerServiceClientActorImpl

case class Create(name: String)
case class StartImport(blobStoreId: String)

class MwhManagerServiceActorImpl(mwhManagerService: MwhManagerService) extends Actor {
  def receive = {
    case Create(name: String) =>
      println("MwhManagerServiceActorImpl.Create :")
      val ret = mwhManagerService.create(name)
      sender ! ret
      println("MwhManagerServiceActorImpl.Create : done")

    case StartImport(blobStoreId: String) =>
      println("MwhManagerServiceActorImpl.StartImport :")
      val ret = mwhManagerService.startImport(blobStoreId)
      sender ! ret
      println("MwhManagerServiceActorImpl.StartImport : done")

    case other =>
      println(s"MwhManagerServiceActorImpl : unknown message = '$other'")
  }
}

object MwhManagerServiceActorApp extends App {
  val config = ConfigFactory.load()
  val system = ActorSystem("actorsystemname", config.getConfig("mwhmanagerservice"))

  val modelManagerClientActor = new ModelManagerServiceClientActorImpl(Some(system))
  val modelClientActor = new ModelServiceClientActorImpl(Some(system))

  val bus                   = new BusImpl
  val mwhManagerService     = new MwhManagerServiceImpl(modelManagerClientActor)
  val mwhManagerServiceBus  = new MwhManagerServiceBusImpl(bus, mwhManagerService, modelClientActor)

  val props = Props(classOf[MwhManagerServiceActorImpl], mwhManagerServiceBus)
  val mwhManagerActor = system.actorOf(props, "MwhManagerActor")

  println("MwhManagerServiceActorApp up.")
}