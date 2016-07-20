package de.company.monitor

import de.company.Idler
import de.company.bus.{Bus, EventSubjects}
import de.company.bus.rabbitMQ.BusImpl

class Monitor(bus: Bus) {
  bus.subscribe(EventSubjects.heartbeat + "#", heartbeat)

  def heartbeat(msg: String): Unit = {
    println(msg)
    bus.publish(EventSubjects.ack + "Monitor", "ack")
  }
}

object Monitor extends App with Idler {
  val bus = new BusImpl
  val monitor = new Monitor(bus)
  idle(200)
}
