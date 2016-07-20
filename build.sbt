import com.trueaccord.scalapb.{ScalaPbPlugin => PB}

name := "Store"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "com.rabbitmq" % "amqp-client" % "3.6.2"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.4.7"

libraryDependencies += "com.typesafe.akka" % "akka-remote_2.11" % "2.4.7"

PB.protobufSettings

PB.pythonExecutable in PB.protobufConfig := "C:\\Python27\\python.exe"

PB.runProtoc in PB.protobufConfig := (args => com.github.os72.protocjar.Protoc.runProtoc("-v300" +: args.toArray))
