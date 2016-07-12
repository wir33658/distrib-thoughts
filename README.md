# distrib-thoughts
Possible ways of setting up distributed environments

This project will collect different ways of setting up distributed environments.

In general there will be communication by events and messages (commands).

Different basic services will have wrappers for different communication strategies also using the same service interface. 
Therefore you should not realy know anything about a command was triggered directly, by an event or being called remotely.

The bus examples are based on the Bus-Interface which allows a Pub/Sub and a Request/Reply environment. 
For the actual implementation RabbitMQ is used (https://www.rabbitmq.com/). 
Feel free to implement the Bus-Interface for another bus vendor (e.g. ActiveMQ).

First at all you will need a service definition setup by an interface. 
The core implementation of the service interface will contain the real logic.

The examples below reflect in a more simple way the complete source code.

Some words on the checked-in source code. Startup anything related to the bus OR Akka. Both at the same time doesn't make sense. 
Startable is anything which extends 'App'. To trigger an initial communication use 'TriggerHelper' (This will send an event).

--------------------- Core ------------------------

Interface :

```
trait ServiceA {
  def doSomething(in: String): Future[String]
}
```

Core implementation :

```
class ServiceAImpl extends ServiceA {
  def doSomething(in: String): Future[String] = {
    // ...
    Doing something in the future
  }
}
```

--------------------- Using a Bus ------------------------

Implementation of a Bus-Wrapper:

Server-Part

```
class ServiceABusImpl(bus: Bus, serviceA: ServiceA) extends ServiceA {
  bus.reply("level1.level2.level3.command.DO_SOMETHING_COMMAND", doSomething)         // used for request-reply
  bus.subscribe("level1.level2.level3.event.TRIGGER_DO_SOMETHING_EVENT", doSomething) // the same command could be triggered by an event
  
  def doSomething(in: String): Future[String] = serviceA.doSomething(in)
}
```
bus         : Any implementation of the Bus-Interface

serviceA    : The core implementation of ServiceA



Client-Part

```
class ServiceAClientBusImpl(bus: Bus) extends ServiceA {
  def doSomething(in: String): Future[String] = bus.request("level1.level2.level3.command.DO_SOMETHING_COMMAND", in)
}
```

Some great things about most Message-Busses are :

- Location agnostic, by using subject-namespaces (logical addressing). No peer-to-peer connection config, only to the broker.
- Event-Listenting with wildcards possible. E.g. "level1.level2.#" means get anything send on "level1.level2".
- Load-Balancing out of the box (starting mulitple services on different machines will be handled by the broker)



--------------------- Using Akka-Actors (Events still handled via bus) ------------------------

In this example Akka-Remote is used. To get a better distribution Akka-Cluster can be used.

Implementation of an Akka-Actor-Wrapper:

Server-Part

```
case class DoSomething(in: String)

class ServiceAActorImpl(serviceA: ServiceA) extends Actor {
  def receive = {
    case DoSomething(in: String) => serviceA.doSomething(in).map(sender ! _)
  }
}

object ServiceA extends App {
  val serviceA = new ServiceAImpl
  val config = ConfigFactory.load()
  val system = ActorSystem("somename", config.getConfig("servicea"))
  val props  = Props(classOf[ServiceAActorImpl], serviceA)
  val actor  = system.actorOf(props, "ServiceAActor")
}
```
serviceA    : The core implementation of ServiceA



Client-Part

```
class ServiceAClientActorImpl(sys: Option[ActorSystem] = None) extends ServiceA {
  val system = sys.getOrElse(ActorSystem("somename"))
  val serviceActor = system.actorSelection("akka.tcp://USER@HOST:PORT/user/ServiceAActor")
  
  def doSomething(in: String): Future[String] = {
    serviceActor ? DoSomething(in).map(println)
  }
}
```

Disadvantages torwards the bus implementation :
- Peer-To-Peer configuration necessary (not location agnostic). Less flexible.
- No wildcards
- Stuck with Java/Scala and Lightbend (company behind Akka)

Advantages in general :
- Separation of concern through Actor-Model
- Serialization of messages out of the box (case classes)
- No single point of failure.




