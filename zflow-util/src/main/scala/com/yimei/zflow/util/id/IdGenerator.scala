package com.yimei.zflow.util.id

import akka.actor.Props

/**
  * Created by hary on 16/12/29.
  */
// create IdGenerator Props
object IdGenerator {
  def props(name: String, persist: Boolean = true) = persist match {

    // 加backoff
    //    case true => BackoffSupervisor.props(
    //      Backoff.onStop(
    //        Props(new PersistentIdGenerator(name)),
    //        childName = "idg",
    //        minBackoff = 3.seconds,
    //        maxBackoff = 30.seconds,
    //        randomFactor = 0.2 // adds 20% "noise" to vary the intervals slightly
    //      ))
    case true => Props(new PersistentIdGenerator(name))
    case false => Props(new MemoryIdGenerator(name))
  }
}
