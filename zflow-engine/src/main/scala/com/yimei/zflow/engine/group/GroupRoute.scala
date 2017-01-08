package com.yimei.zflow.engine.group

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._

/**
  * Created by hary on 17/1/7.
  */
trait GroupRoute extends GroupService with SprayJsonSupport {

  /**
    * 查询组任务
    *
    * GET /gtask?ggid=:ggid& flowType=:flowType& page=:page& pageSize=:pageSize
    */
  private def listTask = get {
    complete("OK")
  }

  /**
    * claim任务
    * GET /gtask/:taskId
    */
  private def claimTask = get {
    complete("ok")
  }

  def groupRoute = listTask ~ claimTask
}
