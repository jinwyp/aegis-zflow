package com.yimei.zflow.cluster

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

/**
  * Created by hary on 17/1/10.
  */
object Test extends App {

  // Override the configuration of the port
  val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + 2551).withFallback(ConfigFactory.load())

  // Create an Akka system
  val system = ActorSystem("ClusterSystem", config)

  println("hello world")
}
