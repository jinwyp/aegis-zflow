package com.yimei.zflow.engine.flow

import java.util.{Date, UUID}

import akka.actor.{ActorRef, Props, ReceiveTimeout}
import akka.event.Logging
import akka.persistence.{PersistentActor, RecoveryCompleted, SaveSnapshotSuccess, SnapshotOffer}
import com.yimei.zflow.api.GlobalConfig._
import com.yimei.zflow.api.models.flow.FlowProtocol
import com.yimei.zflow.engine.FlowRegistry
import com.yimei.zflow.engine.updater.Updater.{FlowStateUpdate, FlowVertexUpdate}

import scala.concurrent.duration._
import akka.pattern._

object PersistentFlow {
  def props(modules: Map[String, ActorRef]) = Props(new PersistentFlow(modules))
}

/**
  * Created by hary on 16/12/6.
  */
class PersistentFlow(modules: Map[String, ActorRef]) extends AbstractFlow
  with PersistentActor
  with FlowProtocol {

  val log = Logging(context.system.eventStream, this)

  import com.yimei.zflow.api.models.flow._

  val flowId = self.path.name

  // flowType!guid!pid
  val regex = "([^!]+)!([^!]+)!(.*)".r
  val (flowType, guid, pid, graph) = flowId match {
    case regex(xflowType, xguid, xpid) => (xflowType, xguid, xpid, FlowRegistry.flowGraph(xflowType))
    case _ => throw new Exception("xxxxx")
  }

  //
  override def persistenceId: String = pid

  // 钝化超时时间
  val timeout = graph.timeout

  log.info(s"timeout is $timeout")
  context.setReceiveTimeout(timeout.seconds)

  override var state = State(flowId, guid, Map(), Map("start" -> true), Nil, graph.flowType)

  //
  override def genGraph(state: State): Graph = graph.graph(state)

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
    case cmd@CommandCreateFlow(flowId, initData) =>
      log.info(s"received ${cmd}")
      if (initData.size > 0 ) {
        val points = initData.map { entry =>
          (entry._1, DataPoint(entry._2, None, None, "sys", new Date().getTime))
        }
        persist(PointsUpdated(points)) { event =>
          updateState(event)
        }
      }
      makeDecision(state.edges.keys.head) // 用当前的edges开始决策!!!!
      sender() ! state

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
    if (e.check(state)) {
      persist(EdgeCompleted(name)) { event =>
        updateState(event)

        lazy val edgesNotHasInEdge =
          !graph.inEdges(e.end)
          .exists(state.edges.contains(_))

        lazy val allPointDataCollected = graph.inEdges(e.end)
          .map(graph.edges(_))
          .foldLeft(true)((b, s) => b && s.check(state))

        if (edgesNotHasInEdge && allPointDataCollected) {
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

    // 更新节点
    modules(module_updater) ! FlowVertexUpdate(state, e.end)

    persist(DecisionUpdated(e.end, arrows)) { event =>

      updateState(event)

      arrows.foreach { arr =>
        arr match {

          case Arrow(name, None) =>
            logState(s"$name 结束")
            saveSnapshot(state)
            modules(module_updater) ! FlowStateUpdate(state)

          case a@Arrow(j, Some(nextEdge)) =>
            val ne = graph.edges(nextEdge)
            if (ne.check(state)) {
              // true边是无法触发的!!!!
              //先删除这个边
              persist(EdgeCompleted(nextEdge)) { event =>
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
