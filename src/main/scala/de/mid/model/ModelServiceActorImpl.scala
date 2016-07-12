package de.mid.model

import akka.actor.{Props, ActorSystem, Actor}
import com.typesafe.config.ConfigFactory
import de.mid.blobstore._
import de.mid.bus.EventSubjects
import de.mid.bus.rabbitMQ.BusImpl

case class Switch()
case class Get(id: String)

class ModelServiceActorImpl(modelService: ModelService) extends Actor {
  import scala.concurrent.ExecutionContext.Implicits.global

  def receive = {
    case Switch() =>
      println("ModelServiceActorImpl.Swtitch")
      modelService.switch().map(sender ! _)

    case Get(id: String) =>
      println("ModelServiceActorImpl.Get")
      modelService.get(id).map(sender ! _)

    case other =>
      println(s"ModelServiceActorImpl : unknown message = '$other'")
  }
}

object ModelServiceActorApp extends App {
  val bus             = new BusImpl
  val modelService    = new ModelServiceImpl
  val modelServiceBus = new ModelServiceBusImpl(bus, modelService)

  val config          = ConfigFactory.load()
  val system          = ActorSystem("smartfacts", config.getConfig("modelservice"))
  val props           = Props(classOf[ModelServiceActorImpl], modelServiceBus)
  val modelActor      = system.actorOf(props, "ModelActor")

  println("ModelServiceActorApp up.")
}