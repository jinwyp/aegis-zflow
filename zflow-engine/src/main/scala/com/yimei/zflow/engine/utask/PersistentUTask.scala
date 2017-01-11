package com.yimei.zflow.engine.utask

import java.util.UUID

import akka.actor.{ActorLogging, ActorRef, ReceiveTimeout}
import akka.persistence.{PersistentActor, RecoveryCompleted, SnapshotOffer}
import com.yimei.zflow.api.models.flow.{CommandPoints, DataPoint}
import com.yimei.zflow.api.models.user._
import com.yimei.zflow.api.GlobalConfig._

import scala.concurrent.duration._

class PersistentUTask(modules: Map[String, ActorRef],
                      passivateTimeout: Long) extends AbstractUTask
  with PersistentActor
  with ActorLogging {

  val guid = self.path.name

  println(s"create persistenter user with guid = $guid")

  // 用户id与用户类型
  //  val regex = "([^!]+)!(.*)".r
  //  val (userType, userId) = guid match {
  //    case regex(utype, uid) => (utype, uid)
  //  }

  override def persistenceId = guid

  var state: State = State(guid, Map[String, CommandUserTask]()) // 用户的状态不断累积!!!!!!!!

  // 超时
  context.setReceiveTimeout(passivateTimeout.seconds)

  // 恢复
  def receiveRecover = {
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
  def uuid() = UUID.randomUUID().toString;

  override def receiveCommand: Receive = commonBehavior orElse serving

  def serving: Receive = {

    // 采集数据请求
    case command: CommandUserTask =>
      log.info(s"收到采集任务: $command")
      val taskId = uuid; // 生成任务id, 将任务保存
      persist(TaskEnqueue(taskId, command)) { event =>
        updateState(event)
        // todo 如果用mobile在线, 给mobile推送采集任务!!!!!!!!!!!!!!!!!!!!
      }

    // 收到用户提交的采集数据
    case command@CommandTaskSubmit(userId, taskId, points: Map[String, DataPoint]) =>
      log.info(s"收到采集提交: $command")
      val taskOpt = state.tasks.get(taskId)
      taskOpt match {
        case Some(task) => persist(TaskDequeue(taskId)) {
          event =>
            updateState(event)
            modules(module_flow) ! CommandPoints(task.flowId, points)
            sender() ! state
        }
        case None =>
          log.warning(s"任务${taskId}已被提交")
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

