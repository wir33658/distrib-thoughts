package de.company.mwhmanager

import de.company.bus.{Commands, CommandSubjects, Bus}

import scala.concurrent.Future

class MwhManagerServiceClientBusImpl(bus: Bus) extends MwhManagerService {
  import scala.concurrent.ExecutionContext.Implicits.global

  def create(name: String): Future[String] = {
    bus.request(CommandSubjects.mwhManagerBase + Commands.MwhManagerCreateCmd, name)
  }

  def startImport(blobStoreId: String): Future[Boolean] = {
    bus.request(CommandSubjects.mwhManagerBase + Commands.MwhManagerStartImportCmd, "") map (in => true)
  }
}

