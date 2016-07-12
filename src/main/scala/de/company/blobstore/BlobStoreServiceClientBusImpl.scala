package de.company.blobstore

import de.company.bus.{Commands, CommandSubjects, Bus}

import scala.concurrent.Future

class BlobStoreServiceClientBusImpl(bus: Bus) extends BlobStoreService {

  def get(modelId: String):Future[String] = {
    bus.request(CommandSubjects.blobStoreBase + Commands.BlobStoreGetCmd, modelId)
  }

  def store(data: String):Future[String] = {
    bus.request(CommandSubjects.blobStoreBase + Commands.BlobStoreStoreCmd, data)
  }
}