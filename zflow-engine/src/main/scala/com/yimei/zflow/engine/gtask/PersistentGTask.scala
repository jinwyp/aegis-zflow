package com.yimei.zflow.engine.gtask

import java.util.UUID

import akka.actor.{ActorLogging, ActorRef, ReceiveTimeout}
import akka.persistence.{PersistentActor, RecoveryCompleted, SnapshotOffer}
import com.yimei.zflow.api.GlobalConfig.module_utask
import com.yimei.zflow.api.models.user.CommandUserTask

import scala.concurrent.duration._


/**
  * Created by hary on 16/12/10.
  */

class PersistentGTask(modules: Map[String, ActorRef], timeout: Int) extends AbstractGTask with PersistentActor with ActorLogging {

  import com.yimei.zflow.api.models.group._


  val ggid = self.path.name

  println(s"create persistenter group with ggid = $ggid")

  override def persistenceId = ggid

  override var state: State = State(ggid, Map[String, CommandGroupTask]()) // group的状态不断累积!!!!!!!!

  // 超时
  context.setReceiveTimeout(timeout seconds)


  // 恢复
  override def receiveRecover = {
    case ev: Event =>
      log.info(s"recover with event: $ev")
      updateState(ev)

    case SnapshotOffer(_, snapshot: State) =>
      log.info(s"recover with snapshot: $snapshot")
      state = snapshot

    case RecoveryCompleted =>
      log.info(s"recover completed")
  }

  // 生成任务id
  def uuid() = UUID.randomUUID().toString

  override def receiveCommand: Receive = commonBehavior orElse serving


  def serving: Receive = {

    case command: CommandGroupTask =>
      log.info(s"收到采集任务: $command")
      val taskId = uuid; // 生成任务id, 将任务保存
      persist(TaskEnqueue(taskId, command)) { event =>
        updateState(event)
      }

    // todo: 王琦, userId should be guid, we don't know userType, also ggid不应该被split为 userType + gid
    case command@CommandClaimTask(ggid: String, taskId: String, guid: String) =>
      log.info(s"claim的请求: $command")
      state.tasks.get(taskId) match {
        case Some(task) => persist(TaskDequeue(taskId)) {
          event =>
            updateState(event)
            modules(module_utask) ! CommandUserTask(task.flowId, guid, task.taskName, task.flowType)
            sender() ! state
        }
        case None =>
          log.warning(s"task[$taskId] is already claimed")
      }

    // 收到超时
    case ReceiveTimeout =>
      log.info(s"${persistenceId}超时, 开始钝化!!!!")
      context.stop(self)
  }

  override def unhandled(message: Any): Unit = {
    log.error(s"收到未处理消息 $message")
    super.unhandled(message)
  }

}
