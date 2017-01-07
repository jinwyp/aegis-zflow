package com.yimei.zflow.util.id

import akka.actor.Props

/**
  * Created by hary on 16/12/29.
  */
// create IdGenerator Props
object IdGenerator {
  def props(name: String, nodeId: Int = 0, persist: Boolean = true) = persist match {

    //  import akka.actor.{Actor, ActorLogging}

    case true => Props(new PersistentIdGenerator(name, nodeId))
    case false => Props(new MemoryIdGenerator(name, nodeId))
  }
}
