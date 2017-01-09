package com.yimei.zflow.engine.flow

import akka.pattern._
import akka.util.Timeout
import com.yimei.zflow.api.models.flow.{CommandCreateFlow, CommandFlowGraph, CommandFlowState, CommandHijack, CommandUpdatePoints, DataPoint, Graph, Command => FlowCommand, State => FlowState}
import com.yimei.zflow.engine.FlowRegistry

import scala.concurrent.Future

/**
  * Created by hary on 17/1/6.
  */
trait FlowService {

  val flowServiceTimeout: Timeout

  import FlowRegistry.flow

  import scala.concurrent.ExecutionContext.Implicits.global

  // 1> 创建流程 - 自动运行
  // 2> 查询流程
  // 3> 管理员更新数据点

  def flowCreate(guid: String, flowType: String, init: Map[String, String] = Map()): Future[FlowState] =
    if (flow != null) {
      (flow ? CommandCreateFlow(flowType, guid, init)) (flowServiceTimeout).mapTo[FlowState]
    } else {
      Future {
        throw new Exception("flow is not prepared")
      }
    }

  def flowGraph(flowId: String): Future[Graph] =
    if (flow != null) {
      (flow ? CommandFlowGraph(flowId)) (flowServiceTimeout).mapTo[Graph]
    } else {
      Future {
        throw new Exception("flow is not prepared")
      }
    }

  def flowState(flowId: String): Future[FlowState] =
    if (flow != null) {
      (flow ? CommandFlowState(flowId)) (flowServiceTimeout).mapTo[FlowState]
    } else {
      Future {
        throw new Exception("flow is not prepared")
      }
    }

  def flowUpdatePoints(flowId: String, updatePoint: Map[String, String], trigger: Boolean): Future[FlowState] =
    if (flow != null) {
      (flow ? CommandUpdatePoints(flowId, updatePoint, false)) (flowServiceTimeout).mapTo[FlowState] // todo
    } else {
      Future {
        throw new Exception("flow is not prepared")
      }
    }

  def flowHijack(flowId: String, updatePoints: Map[String, DataPoint], decision: Option[String], trigger: Boolean): Future[FlowState] =
    if (flow != null) {
      (flow ? CommandHijack(flowId, updatePoints, decision, trigger)) (flowServiceTimeout).mapTo[FlowState]
    } else {
      Future {
        throw new Exception("flow is not prepared")
      }
    }
}
