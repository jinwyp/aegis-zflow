package com.yimei.zflow.api.models.user

import java.sql.Timestamp
import java.text.SimpleDateFormat

import akka.actor.ActorRef
import com.yimei.zflow.api.models.flow.{DataPoint, FlowProtocol}
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

trait Command {
  def guid: String
}

// 0. 创建用户 for UserMaster
case class CommandCreateUser(guid: String) extends Command

// 1. 用户提交任务
case class CommandTaskSubmit(guid: String, taskId: String, points: Map[String, DataPoint]) extends Command

// 2. shutdown用户
case class CommandShutDown(guid: String) extends Command

// 3. 手机登录成功
//case class CommandMobileCome(guid: String, mobile: ActorRef) extends Command

// 4. 电脑登录
//case class CommandDesktopCome(guid: String, desktop: ActorRef) extends Command

// 5. 查询用户信息
case class CommandQueryUser(guid: String) extends Command

// 采集用户数据
case class CommandUserTask(flowId: String, guid: String, taskName: String,flowType:String)

////////////////////////////////////////////////////
// 事件
////////////////////////////////////////////////////
trait Event

// 将采集任务保存
case class TaskEnqueue(taskId: String, task: CommandUserTask) extends Event

// 将采集任务删除
case class TaskDequeue(taskId: String) extends Event

////////////////////////////////////////////////////
// 状态
////////////////////////////////////////////////////
case class State(userId: String, userType: String, tasks: Map[String, CommandUserTask])

trait UserProtocol extends DefaultJsonProtocol with FlowProtocol {

  implicit object TimeStampJsonFormat extends RootJsonFormat[Timestamp] {

    val formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    override def write(obj: Timestamp) = JsString(formatter.format(obj))

    override def read(json: JsValue) : Timestamp = json match {
      case JsString(s) => new Timestamp(formatter.parse(s).getTime)
      case _ => throw new DeserializationException("Error info you want here ...")
    }
  }

  implicit val userCommandUserTaskFormat = jsonFormat4(CommandUserTask)

  implicit val userStateFormat = jsonFormat3(State)



  implicit val CommandCreateUserFormat = jsonFormat1(CommandCreateUser)

  implicit val CommandTaskSubmitFormat = jsonFormat3(CommandTaskSubmit)

  implicit val CommandShutDownFormat = jsonFormat1(CommandShutDown)

//  implicit val CommandMobileComeFormat = jsonFormat2(CommandMobileCome)

//  implicit val CommandDesktopComeFormat = jsonFormat2(CommandDesktopCome)

  implicit val CommandQueryUserFormat = jsonFormat1(CommandQueryUser)

  implicit val TaskEnqueueFormat = jsonFormat2(TaskEnqueue)

  implicit val TaskDequeueFormat = jsonFormat1(TaskDequeue)


}