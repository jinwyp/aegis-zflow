package com.yimei.zflow.engine.gtask

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._

/**
  * Created by hary on 17/1/7.
  */
trait GTaskRoute extends SprayJsonSupport with GTaskService {


  /**
    * 查询组任务
    *
    * GET /gtask?ggid=:ggid& flowType=:flowType& page=:page& pageSize=:pageSize
    */
  private def listTask = get {
    path("gtask") {
      parameters("ggid", "flowType", "page".as[Int], "pageSize".as[Int]) {(ggid, flowType, page, pageSize) =>
        complete(s"ggid = $ggid, flowType = $flowType, page = $page, pageSize = $pageSize")
      }
    }
  }

  /**
    * claim任务
    * GET /gtask/:taskId
    */
  private def claimTask = get {
    path("gtask" / Segment) { taskId =>
      complete(s"taskId = $taskId")
    }
  }

  def gtaskRoute = listTask ~ claimTask
}
