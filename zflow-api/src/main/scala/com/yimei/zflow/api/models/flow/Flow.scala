package com.yimei.zflow.api.models.flow

import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.ActorRef
import com.yimei.zflow.single.config.GlobalConfig._
import FlowRegistry._
import com.yimei.zflow.engine.FlowRegistry
import com.yimei.zflow.engine.group.GroupMaster.gfetch
import com.yimei.zflow.engine.user.UserMaster.ufetch
import com.yimei.zflow.engine.auto.AutoMaster._
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

// 数据点: 值, 说明, 谁采集, 采集id, 采集时间
case class DataPoint(value: String, memo: Option[String], operator: Option[String], id: String, timestamp: Long, used: Boolean = false)

// create flow, but not run it
case class CommandCreateFlow(flowType: String, guid: String, initData: Map[String, String] = Map())

// response of CommandCreateFlow
case class CreateFlowSuccess(flowId: String)

// 接收命令
trait Command {
  def flowId: String // flowType-userType-userId-uuid
}

// 启动流程
case class CommandRunFlow(flowId: String) extends Command

// 停止流程
case class CommandShutdown(flowId: String) extends Command

// 收到数据点
case class CommandPoint(flowId: String, name: String, point: DataPoint) extends Command

// 收到数据点集合  -- 同质数据点
case class CommandPoints(flowId: String, points: Map[String, DataPoint]) extends Command

// 查询流程
case class CommandFlowGraph(flowId: String) extends Command

// 查询流程状态
case class CommandFlowState(flowId: String) extends Command

// 手动更新points
case class CommandUpdatePoints(flowId: String, points: Map[String, String], trigger: Boolean) extends Command

// 流程劫持
case class CommandHijack(flowId: String,
                         points: Map[String, DataPoint],
                         decision: Option[String],
                         trigger: Boolean) extends Command

// persistent事件
trait Event

case class PointUpdated(name: String, point: DataPoint) extends Event

case class PointsUpdated(pionts: Map[String, DataPoint]) extends Event

case class EdgeCompleted(name: String) extends Event

case class DecisionUpdated(name: String, arrow: Seq[Arrow]) extends Event

case class Hijacked(points: Map[String, DataPoint], decision: Option[String]) extends Event

// 状态
case class State(
                  flowId: String,
                  guid: String,
                  points: Map[String, DataPoint],
                  edges: Map[String, Boolean],     // edges to be eliminated
                  histories: Seq[String],          // edges already eliminated
                  flowType: String,
                  ending:Option[String] = None
                )


case class PartUTask(guidKey: String, tasks: Seq[String])

case class PartGTask(ggidKey: String, tasks: Seq[String])

trait EdgeBehavior {
  val name: String
  val autoTasks: Seq[String]
  val userTasks: Seq[String]
  val partUTasks: Seq[PartUTask]
  val partGTasks: Seq[PartGTask] // key = 参与方ggid对应的流程上下文key值, value = userTask名称列表

  /*
    * 调度采集数据
    *
    * @param state   流程状态
    * @param modules 这个流程依赖的模块
    */
  def schedule(state: State, modules: Map[String, ActorRef]) = {

    if (name == "Start") {
      throw new IllegalArgumentException("StartEdge can not be scheduled")
    }

    //采集自动任务
    autoTasks.foreach(at =>
      fetch(state.flowType, at, state, modules(module_auto))
    )

    //采集用户任务
    userTasks.foreach(ut =>
      ufetch(state.flowType, ut, state, modules(module_user), state.guid)
    )

    // 参与方任务
    partUTasks.foreach { entry =>
      entry.tasks.foreach { ut =>
        ufetch(state.flowType, ut, state, modules(module_user), state.points(entry.guidKey).value)
      }
    }

    // 参与方组任务
    partGTasks.foreach { entry =>
      entry.tasks.foreach { gt =>
        gfetch(state.flowType, gt, state, modules(module_group), state.points(entry.ggidKey).value)
      }
    }
  }

  /**
    *
    * @param state
    * @return
    */
  def check(state: State): Boolean = {

    // 没有任何任务!!!
    if (autoTasks.length == 0 &&
      userTasks.length == 0 &&
      partUTasks.size == 0 &&
      partGTasks.size == 0
    ) {
      true
    }

    val pUserTasks: Seq[String] = partUTasks.foldLeft(Seq[String]())((t, put) => t ++: put.tasks)
    val pGroupTasks: Seq[String] = partGTasks.foldLeft(Seq[String]())((t, gut) => t ++: gut.tasks)
    val allUserTasks: Seq[String] = userTasks ++: pUserTasks ++: pGroupTasks

    //对于指定的flowType和taskName 所需要的全部数据点， 如果当前status中的未使用过的数据点没有完全收集完，就返回false
    autoTasks.foldLeft(true)((t, at) => t && !registries(state.flowType).autoTasks(at).points.exists(!state.points.filter(t => (!t._2.used)).contains(_))) &&
      allUserTasks.foldLeft(true)((t, ut) => t && !registries(state.flowType).userTasks(ut).points.exists(!state.points.filter(t => (!t._2.used)).contains(_)))
  }

  //获取全部不能重用的task
  def getNonReusedTask(): (Seq[String], Seq[String]) = {
    val pUserTasks: Seq[String] = partUTasks.foldLeft(Seq[String]())((t, put) => t ++: put.tasks)
    val pGroupTasks: Seq[String] = partGTasks.foldLeft(Seq[String]())((t, gut) => t ++: gut.tasks)
    val allUserTasks: Seq[String] = userTasks ++: pUserTasks ++: pGroupTasks
    (autoTasks, allUserTasks)
  }

  /**
    * 根据（autoTask,userTask) 获取全部的数据点
    *
    * @return
    */
  def getAllDataPointsName(state: State): Seq[String] = {
    val allTasks = getNonReusedTask()
    allTasks._1.foldLeft(Seq[String]())((a, at) => registries(state.flowType).autoTasks(at).points ++: a) ++
      allTasks._2.foldLeft(Seq[String]())((a, ut) => registries(state.flowType).userTasks(ut).points ++: a)
  }
}

//
// 分支边
// partUTasks:
//   key为流程上下文的值, 这里的意思是: 对于这个map中的每个key, 将从上下文中取出这个key对应的值, 取出来的值是某个参与方的guid,
//   value为, 这个参与方需要作的任务列表
//
// partGTasks:
//   key为流程上下文的值, 这里的意思是: 对于这个map中的每个key, 将从上下文中取出这个key对应的值, 取出来的值是某个参与方的ggid(参与方运营组)
//   value为, 这个参与方运营组需要作的任务列表
//
// common judges
//  val FlowSuccess = "FlowSuccess"
//  val FlowFail = "FlowFail"
//  val FlowTodo = "FlowTodo"

case class Edge(
                 autoTasks: Seq[String] = List(),
                 userTasks: Seq[String] = List(),
                 partUTasks: Seq[PartUTask] = List(),
                 partGTasks: Seq[PartGTask] = List(),
                 name: String,
                 end: String,
                 begin: String = "God"
               ) extends EdgeBehavior

case class TaskInfo(description: String, points: Seq[String])

case class Graph(
                  edges: Map[String, Edge],
                  vertices: Map[String, String],
                  state: Option[State],
                  points: Map[String, String],
                  userTasks: Map[String, TaskInfo],
                  autoTasks: Map[String, TaskInfo]
                )

//case class Arrow(end: String, edge: Option[Edge])
case class Arrow(end: String, edge: Option[String])

trait FlowProtocol extends DefaultJsonProtocol {

  // 日期
  implicit object DateJsonFormat extends RootJsonFormat[Date] {
    val formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")   // todo change format

    override def write(obj: Date) = JsString(formatter.format(obj))

    override def read(json: JsValue): Date = json match {
      case JsString(s) => formatter.parse(s)
      case _ => throw new DeserializationException("Error info you want here ...")
    }
  }
  implicit val dataPointFormat = jsonFormat6(DataPoint)

  implicit val commandHijackFormat = jsonFormat4(CommandHijack)

  implicit val partUTaskFormat = jsonFormat2(PartUTask)

  implicit val partGTaskFormat = jsonFormat2(PartGTask)

  implicit val edgeFormat = jsonFormat7(Edge)

  implicit val arrowFormat =jsonFormat2(Arrow)

  implicit val stateFormat = jsonFormat7(State)

  implicit val taskInfoFormat = jsonFormat2(TaskInfo)

  implicit val graphFormat = jsonFormat6(Graph)

  implicit val commandCreateFlowFormat = jsonFormat3(CommandCreateFlow)

  implicit val createFlowSuccessFormat = jsonFormat1(CreateFlowSuccess)

  implicit val commandRunFlowFormat = jsonFormat1(CommandRunFlow)

  implicit val commandShutdownFormat = jsonFormat1(CommandShutdown)

  implicit val commandPointFormat = jsonFormat3(CommandPoint)

  implicit val commandPointsFormat = jsonFormat2(CommandPoints)

  implicit val commandFlowGraphFormat = jsonFormat1(CommandFlowGraph)

  implicit val commandFlowStateFormat = jsonFormat1(CommandFlowState)

  implicit val commandUpdatePointsFormat = jsonFormat3(CommandUpdatePoints)

  implicit val pointUpdatedFormat = jsonFormat2(PointUpdated)

  implicit val pointsUpdatedFormat = jsonFormat1(PointsUpdated)

  implicit val EdgeCompletedFormat = jsonFormat1(EdgeCompleted)

  implicit val DecisionUpdatedFormat = jsonFormat2(DecisionUpdated)

  implicit val HijackedFormat = jsonFormat2(Hijacked)

}
