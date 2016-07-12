package de.mid.monitor

import de.mid.Idler
import de.mid.bus.EventSubjects
import de.mid.bus.rabbitMQ.BusImpl

object Monitor extends App with Idler {
  val bus = new BusImpl
  bus.subscribe(EventSubjects.heartbeat + "#", heartbeat)
  idle(200)

  def heartbeat(msg: String): Unit = {
    println(msg)
  }
}
