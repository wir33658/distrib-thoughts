package de.company.monitor

import de.company.Idler
import de.company.bus.EventSubjects
import de.company.bus.rabbitMQ.BusImpl

object Monitor extends App with Idler {
  val bus = new BusImpl
  bus.subscribe(EventSubjects.heartbeat + "#", heartbeat)
  idle(200)

  def heartbeat(msg: String): Unit = {
    println(msg)
  }
}
