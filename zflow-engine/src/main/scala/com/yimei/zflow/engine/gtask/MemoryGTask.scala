package com.yimei.zflow.engine.gtask

import java.util.UUID

import akka.actor.{ActorLogging, ActorRef}
import com.yimei.zflow.api.GlobalConfig._
import com.yimei.zflow.api.models.user.CommandUserTask

/**
  * Created by hary on 16/12/12.
  */

//object MemoryGroup {
//  //def props(userType: String, modules: Map[String, ActorRef]): Props = Props(new PersistentGroup(userType, modules))
//}

class MemoryGTask(modules:Map[String,ActorRef]) extends AbstractGTask with ActorLogging {
  import com.yimei.zflow.api.models.group._

  val ggid = self.path.name

  override var state: State = State(ggid, Map[String,CommandGroupTask]()) // group的状态不断累积!!!!!!!!

  // 生成任务id
  def uuid() = UUID.randomUUID().toString

  override def receive: Receive = commonBehavior orElse serving


  def serving: Receive = {

    // 采集数据请求
    case command: CommandGroupTask =>
      log.info(s"收到采集任务: $command")
      val taskId = uuid; // 生成任务id, 将任务保存
      updateState(TaskEnqueue(taskId, command))
    // todo 如果用mobile在线, 给mobile推送采集任务!!!!!!!!!!!!!!!!!!!!

    // 收到用户claim请求
    case command@CommandClaimTask(ggid: String, taskId: String, userId: String) =>
      log.info(s"claim的请求: $command")
      val task = state.tasks(taskId)
      updateState(TaskDequeue(taskId))
      modules(module_utask) ! CommandUserTask(task.flowId,s"${ggid}",task.taskName,task.flowType)
      sender() ! state
  }

  override def unhandled(message: Any): Unit = {
    log.error(s"收到未处理消息! {} from {}",message,sender())
    super.unhandled(message)
  }
}
