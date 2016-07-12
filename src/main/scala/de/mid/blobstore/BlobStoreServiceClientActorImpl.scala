package de.mid.blobstore

import akka.actor.ActorSystem
import akka.util.Timeout
import scala.concurrent.Future
import scala.concurrent.duration._
import akka.pattern.ask

// Client used somewhere else to trigger the BlobStoreService via Actors.
class BlobStoreServiceClientActorImpl(sys: Option[ActorSystem] = None) extends BlobStoreService {
  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val timeout = Timeout(60 seconds)

  val system = sys.getOrElse(ActorSystem("smartfacts"))
  val blobStoreActor = system.actorSelection("akka.tcp://smartfacts@127.0.0.1:2553/user/BlobStoreActor")

  def store(data: String): Future[String] = {
    blobStoreActor ? Store(data) map {reply =>
      println(s"BlobStoreServiceClientActorImpl.store : reply = '$reply'")
      reply.toString
    }
  }

  def get(id: String): Future[String] = {
    blobStoreActor ? Get(id) map {reply =>
      println(s"BlobStoreServiceClientActorImpl.get : reply = '$reply'")
      reply.toString
    }
  }
}
