package de.company.modelmanager

import de.company.bus.{Commands, CommandSubjects, Bus}

import scala.concurrent.Future

class ModelManagerServiceClientBusImpl(bus: Bus) extends ModelManagerService {
  import scala.concurrent.ExecutionContext.Implicits.global

  def startImport(blobStoreId: String): Future[Boolean] = {
    bus.request(CommandSubjects.modelManagerBase + Commands.ModelManagerStartImportCmd, blobStoreId) map (in => true)
  }
}
