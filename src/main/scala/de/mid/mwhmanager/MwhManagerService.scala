package de.mid.mwhmanager

import de.mid.modelmanager.ModelManagerService

import scala.concurrent.Future

trait MwhManagerService {
  def create(name: String): Future[String]
  def startImport(blobStoreId: String):Future[Boolean]
}

class MwhManagerServiceImpl(modelManagerService: ModelManagerService) extends MwhManagerService {
  import scala.concurrent.ExecutionContext.Implicits.global

  def create(name: String): Future[String] = ???

  def startImport(blobStoreId: String) = {
    println(s"StartImport with BlobStoreId '$blobStoreId'.")
    modelManagerService.startImport(blobStoreId) map { ret =>
      println(s"StartImport done.")
      true
    }
  }
}

