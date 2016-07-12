package de.mid.modelmanager

import akka.actor.ActorSystem
import akka.util.Timeout
import scala.concurrent.Future
import scala.concurrent.duration._
import akka.pattern.ask


// Client used somewhere else to trigger the ModelManagerService via Actors.
class ModelManagerServiceClientActorImpl(sys: Option[ActorSystem] = None) extends ModelManagerService {
  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val timeout = Timeout(60 seconds)

  val system = sys.getOrElse(ActorSystem("smartfacts"))
  val modelManagerActor = system.actorSelection("akka.tcp://smartfacts@127.0.0.1:2555/user/ModelManagerActor")

  def startImport(blobStoreId: String): Future[Boolean] = {
    modelManagerActor ? ModelImportStart(blobStoreId) map {reply =>
      true
    }
  }
}
