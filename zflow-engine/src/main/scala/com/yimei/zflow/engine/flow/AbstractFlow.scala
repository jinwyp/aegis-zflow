package com.yimei.zflow.engine.flow

import akka.actor.{Actor, ActorLogging}
import akka.event.LoggingAdapter
import com.yimei.zflow.engine.graph.FlowGraph

/**
  * some common facilities
  */
abstract class AbstractFlow extends Actor {

  import com.yimei.zflow.api.models.flow._

  val log: LoggingAdapter

  // 数据点名称 -> 数据点值
  var state: State

  val graph: FlowGraph

  //
  def genGraph(state: State): Graph

  //
  def updateState(ev: Event) = {
    //todo 这里需要考虑决策点改变的情况
    ev match {
      case Hijacked(updatePoints,_) =>
        state = state.copy(points = state.points ++ updatePoints)
//        updatePoints match {
//        case Some(v) =>
//          state = state.copy(points = state.points ++ updatePoints)
//        case None =>
//          state = state.copy(points = state.points ++ updatePoints)
//      }
      case PointUpdated(name, point) => state = state.copy(points = state.points + (name -> point))
      case PointsUpdated(map) => state = state.copy(points = state.points ++ map)

      // 边完成
      case EdgeCompleted(name) =>
        log.info(s"edge[$name] completed")
        state = state.copy(
          edges = state.edges - name,
          histories = name +: state.histories
        )

      case DecisionUpdated(name, arrows) =>


        // 将当前点的入边所负责的数据点设置为已使用
        var newPoints = state.points
        graph.inEdges(name)
          .map(graph.edges(_))
          .map(_.getAllDataPointsName(state))
          .foldLeft(Seq[String]())((acc, elem) => acc ++ elem)
          .foreach { ap =>
            newPoints = newPoints + (ap -> newPoints(ap).copy(used = true))
          }

        log.info(s"!!!!!($name, $arrows) ---> ${arrows.filter(_.edge.nonEmpty).map(e => (e.edge.get -> true)).toMap}")

        if (arrows.size == 1 && arrows(0).edge == None) {
          state = state.copy(
            edges = state.edges ++ arrows.filter(_.edge.nonEmpty).map(e => (e.edge.get -> true)).toMap,
            points = newPoints,
            ending = Some(arrows(0).end)
          )
        } else {
          state = state.copy(
            edges = state.edges ++ arrows.filter(_.edge.nonEmpty).map(e => (e.edge.get -> true)).toMap,
            points = newPoints
          )
        }


        log.debug("new status: {}", state)
    }
  }

  def logState(mark: String = ""): Unit = {
    log.info(s"<$mark>current state: { ${state.edges.keys.mkString(",")} [${state.histories.mkString(",")}]} + {${state.points.map(_._1).mkString(",")}} + {${state.guid}}")
  }

  def commonBehavior: Receive = {
    // return the whole graph = state + graph
    case query: CommandFlowGraph =>
      sender() ! genGraph(state)

    // only return the state
    case query: CommandFlowState =>
      sender() ! state

    case shutdown: CommandShutdown =>
      log.info("received CommandShutdown")
      context.stop(self)
  }
}
