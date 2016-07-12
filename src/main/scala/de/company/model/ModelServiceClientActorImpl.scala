package de.company.model

import akka.actor.ActorSystem
import akka.util.Timeout

import scala.concurrent.duration._
import akka.pattern.ask

import scala.concurrent.Future

// Client used somewhere else to trigger the BlobStoreService via Actors.
class ModelServiceClientActorImpl(sys: Option[ActorSystem] = None) extends ModelService {
  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val timeout = Timeout(60 seconds)

  val system = sys.getOrElse(ActorSystem("actorsystemname"))
  val modelActor = system.actorSelection("akka.tcp://actorsystemname@127.0.0.1:2556/user/ModelActor")

  def get(modelId: String): Future[String] = {
    modelActor ? Get(modelId) map { reply =>
      println(s"ModelServiceClientActorImpl.get : reply = '$reply'")
      reply.toString
    }
  }

  def switch(): Future[Boolean] = {
    modelActor ? Switch() map { reply =>
      println(s"ModelServiceClientActorImpl.swtich : reply = '$reply'")
      true
    }
  }
}
