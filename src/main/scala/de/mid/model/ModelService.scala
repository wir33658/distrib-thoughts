package de.mid.model

import scala.concurrent.Future

trait ModelService {
  def get(modelId: String):Future[String]
  def switch():Future[Boolean]
}

class ModelServiceImpl extends ModelService {
  def get(modelId: String):Future[String] = Future.successful{
    println(s"ModelServiceImpl.get : modelId = '$modelId'")
    "A model"
  }
  def switch():Future[Boolean] = Future.successful{
    println(s"ModelServiceImpl.switch")
    true
  }
}
