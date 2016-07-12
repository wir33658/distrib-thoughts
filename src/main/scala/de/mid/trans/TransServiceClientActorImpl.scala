package de.mid.trans

import akka.actor.ActorSystem
import akka.util.Timeout

import scala.concurrent.Future
import akka.pattern.ask
import scala.concurrent.duration._


// Client used somewhere else to trigger the MwhManagerService via Actors.
class TransServiceClientActorImpl(sys: Option[ActorSystem] = None) extends TransService {
  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val timeout = Timeout(60 seconds)

  val system = sys.getOrElse(ActorSystem("smartfacts"))
  val transActor = system.actorSelection("akka.tcp://smartfacts@127.0.0.1:2554/user/TransActor")

  def start(rdf: String): Future[String] = {
    transActor ? TransStart(rdf) map {reply =>
      println(s"TransServiceClientActorImpl.create : reply = '$reply'")
      reply.toString
    }
  }

  def status(): Future[String] = {
    transActor ? Status() map {reply =>
      println(s"TransServiceClientActorImpl.status : reply = '$reply'")
      reply.toString
    }
  }
}
