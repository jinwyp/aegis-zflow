package com.yimei.zflow.engine.flow

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._

/**
  * Created by hary on 17/1/7.
  */
trait FlowRoute extends FlowService with SprayJsonSupport {

  /**
    * 1. 创建流程
    * POST  /flow?guid=:guid&flowType=:flowType
    * @return
    */
  def createFlow() = post {
    complete("ok")
  }

  /**
   * 2. 查询流程          guiid, flowType, status为可选参数, 若page没指定,
   * GET   /flow?guid=:guid&flowType=:flowType&status=:status&page=:page&pageSize=:pageSite
  */
  def listFlow() = get {
    complete("ok")
  }

  /**
   * 3. 查询指定流程
   * GET /flow/:flowId
   */
  def queryFlow() = get {
    complete("ok")
  }

  /**
   * 4. 流程劫持(hijack)
   * PUT /flow/:flowId?trigger=true
   */
  def hijackFlow() = put {
    complete("ok")
  }
}
