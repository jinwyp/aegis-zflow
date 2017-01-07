package com.yimei.zflow.engine.flow

import java.util.{Date, UUID}

import akka.actor.{ActorRef, Props, ReceiveTimeout}
import akka.event.Logging
import akka.persistence.{PersistentActor, RecoveryCompleted, SaveSnapshotSuccess, SnapshotOffer}
import com.yimei.zflow.api.models.flow.FlowProtocol
import com.yimei.zflow.engine.db.FlowInstanceTable
import com.yimei.zflow.engine.graph.FlowGraph
import spray.json._

import scala.concurrent.duration._

object PersistentFlow {

  def props(graph: FlowGraph,
            flowId: String,
            modules: Map[String, ActorRef],
            pid: String,
            guid: String,
            initData: Map[String, String]
           )(jdbcUrl: String, username: String, password: String): Props =
    Props(new PersistentFlow(graph, flowId, modules, pid, guid, initData)(jdbcUrl, username, password))
}

/**
  * Created by hary on 16/12/6.
  */

class PersistentFlow(
                      val graph: FlowGraph,
                      flowId: String,
                      modules: Map[String, ActorRef],
                      pid: String,
                      guid: String,
                      initData: Map[String, String]
                    )(val jdbcUrl: String, val username: String, val password: String) extends AbstractFlow with PersistentActor with FlowInstanceTable with FlowProtocol {

  implicit val coreSystem = context.system

  val log = Logging(context.system.eventStream, this)


  import com.yimei.zflow.api.models.flow._
  import driver.api._

  override def persistenceId: String = pid

  // 钝化超时时间
  val timeout = graph.timeout

  log.info(s"timeout is $timeout")
  context.setReceiveTimeout(timeout seconds)

  // 流程初始化数据
  val initPoints = initData.map { entry =>
    (entry._1, DataPoint(entry._2, None, None, "init", new Date().getTime, false))
  }

  override var state = State(flowId, guid, initPoints, Map("start" -> true), Nil, graph.flowType)

  //
  override def genGraph(state: State): Graph = graph.graph(state)

  //  override def modules: Map[String, ActorRef] = dependOn

  // 恢复
  def receiveRecover = {

    case ev: Event =>
      log.info(s"recover with event: $ev")
      updateState(ev)

    case SnapshotOffer(_, snapshot: State) =>
      state = snapshot
      log.info(s"snapshot recovered")

    case RecoveryCompleted =>
      logState("recovery completed")

  }

  override def receiveCommand: Receive = serving orElse commonBehavior

  // 命令处理
  val serving: Receive = {
    case cmd@CommandRunFlow(flowId) =>
      log.info(s"received ${cmd}")
      saveSnapshot(state)
      sender() ! state
      makeDecision(state.edges.keys.head) // 用当前的edges开始决策!!!!

    case cmd: CommandPoint =>
      log.info(s"received ${cmd.name}")
      processCommandPoint(cmd)

    case cmds: CommandPoints =>
      log.info(s"received ${cmds.points.map(_._1)}")
      processCommandPoints(cmds)

    // 管理员更新数据点驱动流程
    case cmd: CommandUpdatePoints =>

      val uuid = UUID.randomUUID().toString
      val points: Map[String, DataPoint] = cmd.points.map { entry =>
        entry._1 -> DataPoint(entry._2, None, None, uuid, new Date().getTime)
      }

      persist(PointsUpdated(points)) { event =>
        log.info(s"${event} persisted")
        updateState(event)
        if (cmd.trigger) {
          val tocheck = graph.pointEdges(points.keys.head)
          makeDecision(tocheck)
        }
        sender() ! state // 返回流程状态
      }

    case CommandHijack(_, points, updateDecision, trigger) =>

      persist(Hijacked(points, updateDecision)) { event =>
        updateState(event)
        if (trigger) {
          val tocheck = graph.pointEdges(points.keys.head)
          makeDecision(tocheck)
        }
        sender() ! state
      }


    // received 超时
    case ReceiveTimeout =>
      log.info(s"passivate timeout, begin passivating!!!!")
      context.stop(self)

    case SaveSnapshotSuccess(metadata) =>
      log.info(s"snapshot saved successfully")
  }

  override def unhandled(msg: Any): Unit = log.error(s"received unhandled message: $msg")

  protected def processCommandPoint(cmd: CommandPoint) = {
    persist(PointUpdated(cmd.name, cmd.point)) {
      event =>
        log.info(s"${event} persisted")
        updateState(event)
        makeDecision(graph.pointEdges(cmd.name))
    }
  }

  protected def processCommandPoints(cmds: CommandPoints) = {
    persist(PointsUpdated(cmds.points)) {
      event =>
        log.info(s"${event} persisted")
        updateState(event)
        // val tocheck = cmds.points.keys.map(graph.pointEdges(_)).toSet
        val tocheck = graph.pointEdges(cmds.points.keys.head)
        makeDecision(tocheck)
    }
  }

  /**
    */
  protected def makeDecision(name: String) = {
    val e = graph.edges(name)
    if (e.check((state))) {
      persist(EdgeCompleted(name)) { event =>
        updateState(event)

        //val temp: Boolean =  state.points.filter(t=>(!t._2.used)).contains("wang")

        lazy val edgesNotHasInEdge = !graph.inEdges(e.end).exists(state.edges.contains(_))
//        lazy val historyHasInEdge = graph.inEdges(e.end).foldLeft(true)((b,s)=>b&&state.histories.contains(s))
//        lazy val allPointDataCollected = graph.inEdges(e.end)
//          .map(graph.edges(_))
//          .map(_.getAllDataPointsName(state))
//          .foldLeft(true)((b,s) => b && s.foldLeft(true)((b1,s1)=> b1&&state.points.filter(t=>(!t._2.used)).contains(s1)))

        lazy val allPointDataCollected = graph.inEdges(e.end)
          .map(graph.edges(_)).foldLeft(true)((b,s) => b && s.check(state))


        if(edgesNotHasInEdge && allPointDataCollected){
          make(e)
        }
      }
    }
  }

  /**
    * 对每条决策边,
    *
    * @param e
    */
  protected def make(e: Edge): Unit = {

    val arrows = graph.deciders(e.end)(state)

    //更新当前到达点
    val udState = flowInstance.filter(f=>
      f.flow_id === state.flowId
    ).map(f=>(f.state)).update(
      e.end
    )

    dbrun(udState)

    persist(DecisionUpdated(e.end, arrows)) { event =>

      updateState(event)

      arrows.foreach { arr =>
        arr match {

          case Arrow(name, None) =>
            logState(s"$name 结束")
            saveSnapshot(state)
            // 更新数据库 todo王琦
            val pu = flowInstance.filter(f=>
              f.flow_id === state.flowId
            ).map(f=>(f.data,f.finished)).update(
              state.toJson.toString,1
            )

            dbrun(pu)

          case a@Arrow(j, Some(nextEdge)) =>
            val ne = graph.edges(nextEdge)
            if (ne.check(state)) {
              // true边是无法触发的!!!!
              //先删除这个边
              persist(EdgeCompleted(nextEdge)){ event =>
                updateState(event)
                make(ne)
              }
            } else {
              ne.schedule(state, modules) // 这个决策返回边是调度边, 则调度!!!
              logState(s"$a")
            }
        }
      }
    }
  }
}
