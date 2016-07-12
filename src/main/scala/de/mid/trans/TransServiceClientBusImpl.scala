package de.mid.trans

import de.mid.bus.{Commands, CommandSubjects, Bus}
import scala.concurrent.Future

class TransServiceClientBusImpl(bus: Bus) extends TransService {
  def start(rdf: String): Future[String] = {
    bus.request(CommandSubjects.transBase + Commands.TransStartCmd, rdf)
  }

  def status(): Future[String] = {
    bus.request(CommandSubjects.transBase + Commands.TransStatusCmd, "")
  }
}
