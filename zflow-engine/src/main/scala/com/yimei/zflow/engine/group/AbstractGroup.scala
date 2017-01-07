package com.yimei.zflow.engine.group

import akka.actor.{Actor, ActorLogging}

/**
  * Created by hary on 16/12/12.
  */
abstract class AbstractGroup extends Actor with ActorLogging {

  import com.yimei.zflow.api.models.group._

  // 抽象方法
  var state: State

  def updateState(event: Event) = {
    event match {
      case TaskDequeue(taskId) => state = state.copy(tasks = state.tasks - taskId)
      case TaskEnqueue(taskId,task) => state = state.copy(tasks = state.tasks + (taskId -> task))
    }
    log.info(s"${event} persisted, state = ${state}")
  }

  def commonBehavior: Receive = {
    case command:CommandQueryGroup =>
      log.info(s"收到group查询：$command")
      sender() ! state
  }
}
