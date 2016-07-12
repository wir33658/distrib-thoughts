package de.company.modelmanager

import de.company.blobstore.BlobStoreService

import scala.concurrent.Future

trait ModelManagerService {
  def startImport(blobStoreId: String):Future[Boolean]
}

class ModelManagerServiceImpl(blobStoreService: BlobStoreService) extends ModelManagerService {
  import scala.concurrent.ExecutionContext.Implicits.global

  def startImport(blobStoreId: String): Future[Boolean] = {
    println(s"ModelManagerServiceImpl.startImport : Now I do some stuff to import '$blobStoreId'.")

    getData(blobStoreId) map {dataToImport =>
      startImport1(blobStoreId, dataToImport)
      startImport2(blobStoreId, dataToImport)
      importDone(blobStoreId)
      true
    }
  }

  protected def getData(blobStoreId: String): Future[String] = blobStoreService.get(blobStoreId)

  protected def startImport1(blobStoreId: String, dataToImport: String): Unit = {
    Thread.sleep(7000) // The Work - Part 1
    println(s"ModelManagerServiceImpl.startImport1 : Imported into DB 1")
  }

  protected def startImport2(blobStoreId: String, dataToImport: String): Unit = {
    Thread.sleep(7000) // The Work - Part 2
    println(s"ModelManagerServiceImpl.startImport2 : Imported into DB 2")
  }

  protected def importDone(blobStoreId: String): Unit ={
    println("ModelManagerServiceImpl.startImport : I am done.")
  }
}
