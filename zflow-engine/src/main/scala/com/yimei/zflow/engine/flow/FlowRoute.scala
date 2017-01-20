package com.yimei.zflow.engine.flow

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.yimei.zflow.api.models.flow.FlowProtocol
import spray.json.DefaultJsonProtocol
import com.yimei.zflow.engine.flow.Models._

/**
  * Created by hary on 17/1/7.
  */
trait FlowRoute extends SprayJsonSupport
  with FlowService
  with FlowProtocol
  with DefaultJsonProtocol {

  /**
    * 1. 创建流程
    * POST  /flow?guid=:guid&flowType=:flowType
    *
    * @return
    */
  private def createFlow = post {
    path("flow") {
      (parameters("guid", "flowType") & entity(as[Map[String,String]]) ){ (guid, flowType, init) =>
       // todo 现在直接是uuid，后面会用idbuffer修改
        val flowId = flowType + "!" + guid + "!" + UUID.randomUUID().toString
        complete(flowCreate(flowId,flowType,guid,init))
      }
    }
  }

  /**
    * 2. 查询流程 guid, flowType, status为可选参数, 若page没指定,
    * GET   /flow?guid=:guid&flowType=:flowType&status=:status&page=:page&pageSize=:pageSite
    */
  private def listFlow = get {
    path("flow") {
      parameters('guid, 'flowType, 'status.?, 'page.as[Int].?, 'pageSize.as[Int].?) { (guid, flowType, status, page, pageSize) =>
        complete(flowList(guid,flowType,status,page,pageSize))
      }
    }
  }

  /**
    * 3. 查询指定流程
    * GET /flow/:flowId
    */
  private def queryFlow = get {
    path("flow" / Segment) { flowId =>
      complete(flowState(flowId))
    }
  }

  /**
    * 4. 流程劫持(hijack)
    * PUT /flow/:flowId?trigger=true
    */
  private def hijackFlow = put {
    (path("flow" / Segment) & entity(as[HijackEntity])){ (flowId,entity) =>
      complete(flowHijack(flowId,entity.updatePoints,entity.decision,entity.trigger))
    }
  }

  def flowRoute: Route = createFlow ~ listFlow ~ queryFlow ~ hijackFlow
}
