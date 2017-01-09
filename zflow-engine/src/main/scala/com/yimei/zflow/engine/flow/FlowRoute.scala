package com.yimei.zflow.engine.flow

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

/**
  * Created by hary on 17/1/7.
  */
trait FlowRoute extends SprayJsonSupport
//  with FlowService
{

  /**
    * 1. 创建流程
    * POST  /flow?guid=:guid&flowType=:flowType
    *
    * @return
    */
  private def createFlow = post {
    path("flow") {
      parameters("guid", "flowType") { (guid, flowType) =>
        complete(s"createFlow: guid = $guid, flowType = $flowType")
      }
    }
  }

  /**
   * 2. 查询流程          guiid, flowType, status为可选参数, 若page没指定,
   * GET   /flow?guid=:guid&flowType=:flowType&status=:status&page=:page&pageSize=:pageSite
  */
  private def listFlow = get {
    path("flow") {
      parameters('guid, 'flowType, 'status, 'page.as[Int], 'pageSize.as[Int]) { (guid, flowType, status, page, pageSize) =>
          complete(s"listFlow:")
      }
    }
  }

  /**
   * 3. 查询指定流程
   * GET /flow/:flowId
   */
  private def queryFlow = get {
    path("flow" / Segment) {  flowId =>
      complete(s"queryFlow: flowId = $flowId")
    }
  }

  /**
   * 4. 流程劫持(hijack)
   * PUT /flow/:flowId?trigger=true
   */
  private def hijackFlow = put {
    path("flow" / Segment) { flowId =>
      complete(s"hijackFlow: flowId = $flowId")
    }
  }

  def flowRoute: Route = createFlow ~ listFlow ~ queryFlow ~ hijackFlow
}
