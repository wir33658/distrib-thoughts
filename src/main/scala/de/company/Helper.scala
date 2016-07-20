package de.company

import java.util.concurrent.{Executors, TimeUnit}

import de.TransStarterCommand.TransStarterCommand
import de.company.bus._
import de.company.bus.rabbitMQ.BusImpl

import scala.concurrent.Future

/**
  * Created by rtweissm on 28.06.2016.
  */
object TriggerHelper extends App with Idler {
  import scala.concurrent.ExecutionContext.Implicits.global

  val bus: Bus = new BusImpl
//  bus.publish(Subject.transBase + Events.StartTransformationTriggerEvent, "Some bla")
  val cmdName = TransStarterCommand.descriptor.name
  //val cmd = TransStarterCommand().
  bus.request(CommandSubjects.transBase + cmdName, "Some bla") map { reply =>
    println(s"TriggerHelper : reply = '$reply'")
  }
  idle(200)
}

trait Idler {
  def idle(millis: Long): Unit ={
    var counter = 0
    while(true){
      Thread.sleep(millis)
      /* print(".")
      counter += 1
      if(counter % 50 == 1)println()
      */
    }
  }
}

trait Heartbeater {
  def beat(bus: Bus, msg: String): Unit ={
    val scheduler = Executors.newScheduledThreadPool(1)
    scheduler.scheduleAtFixedRate(new Runnable(){
      override def run(): Unit = {
        bus.publish(EventSubjects.heartbeat + Events.HeartbeatEvent, s"Heartbeat : $msg")
      }
    }, 1, 30, TimeUnit.SECONDS)
  }
}


trait Common {
  val command1Subject = "de.mid.test.command.Command1"
  val eventSubject = "de.mid.test.event."
}

object ServerTest1 extends ServerTest("1") with App
object ServerTest2 extends ServerTest("2") with App
object ServerTest3 extends ServerTest("3") with App

class ServerTest(name: String) extends Common {
  import scala.concurrent.ExecutionContext.Implicits.global

  val bus = new BusImpl
  bus.registerReplyCallback(command1Subject, command1)

  println(s"ServerTest $name up")

  def command1(msg: String):Future[String] = Future {
    println(s"ServerTest.command1 : $msg")
    Thread.sleep(30)
    bus.publish(eventSubject + "Event" + name, s"Command1 with msg = '$msg' triggered.")
    s"command1 reply : $msg"
  }
}

object ClientTest1 extends ClientTest("1") with App
object ClientTest2 extends ClientTest("2") with App
object ClientTest3 extends ClientTest("3") with App

class ClientTest(name: String) extends Common {
  import scala.concurrent.ExecutionContext.Implicits.global

  val bus = new BusImpl
  bus.subscribe(eventSubject + "#", eventListener)

  println(s"ClientTest $name up")

  for(r <- 1 to 10){
    val msg = s"message $r (ClientTest $name)"
    println(s"sending $msg")
    bus.request(command1Subject, msg).map(r => println(s"reply : $r")).recover{
      case t: Throwable =>
        t.printStackTrace()
    }
  }

  def eventListener(event: String): Unit = {
    println(s"Event : $event")
  }
}
