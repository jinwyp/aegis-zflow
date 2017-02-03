package com.yimei.zflow.engine.gtask

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import com.yimei.zflow.api.models.gtask.GTaskProtocol

/**
  * Created by hary on 17/1/7.
  */
trait GTaskRoute extends SprayJsonSupport with GTaskService with GTaskProtocol {


  /**
    * 查询组任务
    *
    * GET /gtask?ggid=:ggid& flowType=:flowType& page=:page& pageSize=:pageSize
    */
//  private def listTask = get {
//    path("gtask") {
//      parameters("ggid", "flowType", "page".as[Int], "pageSize".as[Int]) {(ggid, flowType, page, pageSize) =>
//        complete(s"ggid = $ggid, flowType = $flowType, page = $page, pageSize = $pageSize")
//      }
//    }
//  }
    private def listTask = get {
      path("gtask") {
        parameters("ggid", "flowType") {(ggid, flowType) =>
          complete(gtaskQuery(ggid,flowType))
        }
      }
    }

  /**
    * claim任务
    * GET /gtask/:taskId
    */
  private def claimTask = get {
    path("gtask" / Segment  / Segment / Segment ) { (guid,taskId,ggid) =>
      complete(gtaskClaim(ggid,guid,taskId))
    }
  }

  def gtaskRoute = listTask ~ claimTask
}
