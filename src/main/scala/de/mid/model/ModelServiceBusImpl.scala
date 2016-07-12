package de.mid.model

import de.mid.{Heartbeater, Idler}
import de.mid.bus.rabbitMQ.BusImpl
import de.mid.bus.{Commands, CommandSubjects, Bus}

import scala.concurrent.Future

class ModelServiceBusImpl(bus: Bus, modelService: ModelService) extends ModelService {
  import scala.concurrent.ExecutionContext.Implicits.global

  bus.reply(CommandSubjects.modelBase + Commands.ModelGetCmd, get)
  bus.reply(CommandSubjects.modelBase + Commands.ModelSwitchCmd, switchReply)

  def get(modelId: String):Future[String] = modelService.get(modelId)
  def switch():Future[Boolean] =
    modelService.switch()

  def switchReply(dummy: String):Future[String] =
    switch() map (ret => ret.toString)
}

object ModelServiceBusApp extends App with Idler with Heartbeater {
  val bus             = new BusImpl
  val modelService    = new ModelServiceImpl
  val modelServiceBus = new ModelServiceBusImpl(bus, modelService)

  beat(bus, "ModelServiceBusApp")

  idle(200)
}
