package de.mid.mwhmanager

import akka.actor.ActorSystem
import akka.util.Timeout
import scala.concurrent.Future
import akka.pattern.ask
import scala.concurrent.duration._

// Client used somewhere else to trigger the MwhManagerService via Actors.
class MwhManagerServiceClientActorImpl(sys: Option[ActorSystem] = None) extends MwhManagerService {
  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val timeout = Timeout(60 seconds)

  val system = sys.getOrElse(ActorSystem("smartfacts"))
  val mwhManagerActor = system.actorSelection("akka.tcp://smartfacts@127.0.0.1:2554/user/MwhManagerActor")

  def create(name: String): Future[String] = {
    mwhManagerActor ? Create(name) map {reply =>
      println(s"MwhManagerServiceClientActorImpl.create : reply = '$reply'")
      reply.toString
    }
  }

  def startImport(blobStoreId: String): Future[Boolean] ={
    mwhManagerActor ? StartImport(blobStoreId) map {reply =>
      println(s"MwhManagerServiceClientActorImpl.startImport : reply = '$reply'")
      true
    }
  }
}
