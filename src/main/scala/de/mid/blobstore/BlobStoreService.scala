package de.mid.blobstore

import scala.concurrent.Future

trait BlobStoreService {
  def store(data: String):Future[String]
  def get(id:String):Future[String]
}

class BlobStoreServiceImpl extends BlobStoreService {
  var data = ""

  def store(data: String):Future[String] = Future.successful {
    println(s"BlobStoreServiceImpl.store : storing '$data'")
    this.data = data
    Thread.sleep(2000)
    println(s"BlobStoreServiceImpl.store : done")
    "BLOB_12323423"
  }

  def get(id:String):Future[String] = Future.successful {
    println(s"BlobStoreServiceImpl.get : id = '$id'")
    data
  }
}

