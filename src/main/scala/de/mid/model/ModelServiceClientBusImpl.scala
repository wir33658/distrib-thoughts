package de.mid.model

import de.mid.bus.{Commands, CommandSubjects, Bus}

import scala.concurrent.Future

class ModelServiceClientBusImpl(bus: Bus) extends ModelService {
  import scala.concurrent.ExecutionContext.Implicits.global

  def get(modelId: String):Future[String] = {
    bus.request(CommandSubjects.modelBase + Commands.ModelGetCmd, modelId)
  }

  def switch():Future[Boolean] = {
    bus.request(CommandSubjects.modelBase + Commands.ModelSwitchCmd, "") map (in => true)
  }
}