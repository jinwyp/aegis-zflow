package com.yimei.zflow.engine.flow

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.pattern._
import akka.util.Timeout
import com.yimei.zflow.api.models.flow.{CommandCreateFlow, CommandFlowGraph, CommandFlowState, CommandHijack, CommandUpdatePoints, DataPoint, FlowProtocol, Graph, Command => FlowCommand, State => FlowState}
import com.yimei.zflow.engine.FlowRegistry
import com.yimei.zflow.engine.db.Entities.FlowInstanceEntity
import com.yimei.zflow.engine.db.FlowInstanceTable
import com.yimei.zflow.util.exception.DatabaseException
import com.yimei.zflow.engine.flow.Models._
import com.yimei.zflow.util.config.Core

import scala.concurrent.Future
import spray.json._

/**
  * Created by hary on 17/1/6.
  */
trait FlowService extends FlowInstanceTable with FlowProtocol with SprayJsonSupport with Core {

  val flowServiceTimeout: Timeout

  import driver.api._

  //  implicit val organServiceExecutionContext = coreSystem.dispatcher
  import coreSystem.dispatcher

  import FlowRegistry.flow

  //从flowId中提取flowId
  private def extractGuid(flowId: String): String = {
    val regex = "([^!]+)!([^!]+)!(.*)".r
    flowId match {
      case regex(xflowType, xguid, xpid) => xguid
      case _ => throw new Exception(s"flowId格式有误:$flowId")
    }
  }

  // 1> 创建流程 - 自动运行
  // 2> 查询流程
  // 3> 管理员更新数据点

  def flowCreate(flowId: String, flowType: String, guid: String, init: Map[String, String] = Map()): Future[FlowState] = {
    if (flow != null) {
      val state = (flow ? CommandCreateFlow(flowId, init)) (flowServiceTimeout).mapTo[FlowState]

      def insertFlow(s: FlowState) = {

        dbrun(flowInstance returning flowInstance.map(_.id) into ((ft, id) => ft.copy(id = id)) +=
          FlowInstanceEntity(None, s.flowId, flowType, guid, s.toJson.toString, FlowRegistry.flowGraph(s.flowType).flowInitial, 0, None)) recover {
          case e =>
            log.error("{}", e)
            throw DatabaseException("添加流程错误")
        }
      }

      for {
        s <- state
        i <- insertFlow(s)
      } yield s

    } else {
      Future.failed(new Exception("flow is not prepared"))
    }
  }


  private def toFlowInstanceEntry(f: FlowInstanceEntity) = FlowInstanceEntry(f.flow_id, f.flow_type, f.guid, f.data, f.state, f.finished, f.ts_c)

  /**
    * 获取流程列表
    *
    * @param guid
    * @param flowType
    * @param status
    * @param page
    * @param pageSize
    * @return
    */
  def flowList(guid: String, flowType: String, status: Option[String], page: Option[Int], pageSize: Option[Int]): Future[ListFlowResponse] = {
    val (pg, pz) = (page, pageSize) match {
      case (Some(p), Some(s)) if p >=1 && s >= 1 => (p, s)
      case _ => (1, 10)
    }

    val query = flowInstance.filter(f =>
      f.guid === guid &&
        f.flow_type === flowType &&
        List(
          status.map(f.state === _)
        ).collect({ case Some(a) => a })
          .reduceLeftOption(_ && _)
          .getOrElse(true: Rep[Boolean]))

    val flows: Future[Seq[FlowInstanceEntity]] = dbrun(query.drop((pg - 1) * pz).take(pz).result)

    val total = dbrun(query.length.result)

    for {
      fs <- flows
      t <- total
    } yield ListFlowResponse(fs.map(toFlowInstanceEntry(_)), t)

  }


  def flowGraph(flowId: String): Future[Graph] =
    if (flow != null) {
      (flow ? CommandFlowGraph(flowId)) (flowServiceTimeout).mapTo[Graph]
    } else {
      Future.failed(new Exception("flow is not prepared"))
    }

  /**
    * 获得单个流程
    * @param flowId
    * @return
    */
  def flowState(flowId: String): Future[FlowState] =
    if (flow != null) {
      val fl: Future[FlowInstanceEntity] = dbrun(flowInstance.filter(_.flow_id === flowId).result.head) recover {
        case _ => throw new DatabaseException("该流程不存在")
      }

      for {
        f <- fl
        r <- (flow ? CommandFlowState(f.flow_id)) (flowServiceTimeout).mapTo[FlowState]
      } yield r

    } else {
      Future.failed(new Exception("flow is not prepared"))
    }


  def flowUpdatePoints(flowId: String, updatePoint: Map[String, String], trigger: Boolean): Future[FlowState] =
    if (flow != null) {
      (flow ? CommandUpdatePoints(flowId, updatePoint, false)) (flowServiceTimeout).mapTo[FlowState] // todo
    } else {
      Future.failed(new Exception("flow is not prepared"))
    }

  /**
    * hijack
    * @param flowId
    * @param updatePoints
    * @param decision
    * @param trigger
    * @return
    */
  def flowHijack(flowId: String, updatePoints: Map[String, DataPoint], decision: Option[String], trigger: Boolean): Future[FlowState] =
    if (flow != null) {
      val fl: Future[FlowInstanceEntity] = dbrun(flowInstance.filter(_.flow_id === flowId).result.head) recover {
        case _ => throw new DatabaseException("该流程不存在")
      }

      for {
        f <- fl
        r <- (flow ? CommandHijack(f.flow_id, updatePoints, decision, trigger)) (flowServiceTimeout).mapTo[FlowState]
      } yield r
    } else {
      Future.failed(new Exception("flow is not prepared"))
    }
}
