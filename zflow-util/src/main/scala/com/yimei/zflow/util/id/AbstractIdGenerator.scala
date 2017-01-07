package com.yimei.zflow.util.id

import akka.actor.{Actor, ActorLogging}
import com.yimei.zflow.util.id.models._

/**
  * Created by hary on 16/12/12.
  */
trait AbstractIdGenerator extends Actor with ActorLogging {

  var state = State(Map())

  def updateState(event: Event): Long = {
    event match {
      case EventIncrease(key, buffer) =>
        val nextId = if (state.keys.contains(key)) {
          state.keys(key) + buffer
        } else {
          0L
        }
        state = state.copy(keys = state.keys + (key -> nextId))
        nextId
    }
  }

  def logState(mark: String) = {
    log.info(s"<${mark}> cur state: $state")
  }

  def commonBehavior: Receive = {
    case CommandQueryId => sender() ! state
  }

}
