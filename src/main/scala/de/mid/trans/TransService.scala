package de.mid.trans

import de.mid.blobstore.BlobStoreService

import scala.concurrent.Future

trait TransService {
  def start(rdf: String):Future[String]
  def status():Future[String]
}

class TransServiceImpl(blobStoreService: BlobStoreService) extends TransService {
  def start(rdf: String):Future[String] = Future.successful {
    println(s"TransServiceImpl.start : Now I do some stuff to transform '$rdf'.")

    Thread.sleep(5000) // The Work !!!
    blobStoreService.store("TRANSFORMED EXPORT DATA")

    println("TransServiceImpl.start : I am done.")
    "Blob_123234234"
  }

  def status():Future[String] = ???
}
