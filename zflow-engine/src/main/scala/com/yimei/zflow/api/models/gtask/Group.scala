package com.yimei.zflow.api.models.gtask

import spray.json.DefaultJsonProtocol

// Command
trait Command {
  def ggid: String
}

case class CommandCreateGroup(ggid: String) extends Command

case class CommandGroupTask(flowType:String, flowId: String, ggid: String, taskName: String) extends Command

case class CommandClaimTask(ggid: String, taskId: String, guid: String) extends Command

case class CommandQueryGroup(ggid: String) extends Command

// Event
trait Event {
  def taskId: String
}

case class TaskEnqueue(taskId: String, task: CommandGroupTask) extends Event

case class TaskDequeue(taskId: String) extends Event

// State:  group -> taskId -> groupTask
case class State(ggid: String, tasks: Map[String, CommandGroupTask])

trait GTaskProtocol extends DefaultJsonProtocol {

  implicit val groupCommandGroupTaskFormat = jsonFormat4(CommandGroupTask)

  implicit val groupStateFormat = jsonFormat2(State)

  implicit val CommandCreateGroupFormat = jsonFormat1(CommandCreateGroup)

  implicit val CommandClaimTaskFormat = jsonFormat3(CommandClaimTask)

  implicit val CommandQueryGroupFormat = jsonFormat1(CommandQueryGroup)

  implicit val GroupTaskEnqueueFormat = jsonFormat2(TaskEnqueue)

  implicit val GTaskDequeueFormat = jsonFormat1(TaskDequeue)
}
