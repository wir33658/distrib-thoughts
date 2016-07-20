package de.company.monitor

import de.company.bus.EventSubjects
import de.company.bus.rabbitMQ.BusImpl
import org.scalatest.{FlatSpec, Matchers}

class MonitorSuite extends FlatSpec with Matchers {
  val bus = new BusImpl

  "Monitor Service" should "catch a heartbeat event" in {
    var ackMsg = ""
    def ack(msg: String): Unit = ackMsg = msg
    bus.subscribe(EventSubjects.ack + "Monitor", ack)
    val monitor = new Monitor(bus)
    bus.publish(EventSubjects.heartbeat + "MonitorSuite", "Some msg")
    Thread.sleep(1000)
    assert(ackMsg == "ack")
  }
  it should "do the same thing again" in {
    var ackMsg = ""
    def ack(msg: String): Unit = ackMsg = msg
    bus.subscribe(EventSubjects.ack + "Monitor", ack)
    val monitor = new Monitor(bus)
    bus.publish(EventSubjects.heartbeat + "MonitorSuite", "Some msg")
    Thread.sleep(1000)
    assert(ackMsg == "ack")
  }
}
