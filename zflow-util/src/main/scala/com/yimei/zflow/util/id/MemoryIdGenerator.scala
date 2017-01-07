package com.yimei.zflow.util.id

import akka.actor.Props
import com.yimei.zflow.util.id.models.{CommandGetId, EventIncrease, Id}


object MemoryIdGenerator {
  def props(name: String, nodeId: Int = 0): Props = Props(new MemoryIdGenerator(name, nodeId))
}


/**
  * Created by hary on 16/12/9.
  */
class MemoryIdGenerator(name: String, nodeId: Int) extends AbstractIdGenerator {

  override def receive: Receive = commonBehavior orElse serving

  def serving: Receive = {
    case CommandGetId(key, buffer) =>
      val old = updateState(EventIncrease(key, buffer))
      sender() ! Id(old + 1)
  }
}


