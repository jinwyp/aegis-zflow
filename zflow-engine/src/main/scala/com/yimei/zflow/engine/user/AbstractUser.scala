package com.yimei.zflow.engine.user

import akka.actor.{Actor, ActorLogging, ActorRef}

/**
  * Created by hary on 16/12/7.
  */
abstract class AbstractUser extends Actor with ActorLogging {

  import com.yimei.zflow.api.models.user._


  var state: State

  // 多终端在线
  var mobile: ActorRef = null
  var desktop: ActorRef = null

  // 更新用户状态
  def updateState(ev: Event) = {
    ev match {
      case TaskDequeue(taskId) => state = state.copy(tasks = state.tasks - taskId)
      case TaskEnqueue(taskId, task) => state = state.copy(tasks = state.tasks + (taskId -> task))
    }
    log.info(s"${ev} persisted, state = ${state}")
  }

  // 公用行为, 不涉及事件处理
  val commonBehavior: Receive = {
    // 用户查询
    case command: CommandQueryUser =>
      log.info(s"收到用户查询: $command")
      sender() ! state

//    // 手机登录成功
//    case command: CommandMobileCome =>
//
//    // 电脑登录成功
//    case command: CommandDesktopCome =>
  }

}


