package com.yimei.zflow.engine.utask

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.yimei.zflow.api.models.utask.UTaskProtocol
import com.yimei.zflow.engine.utask.Models.UserSubmitEntity

/**
  * Created by hary on 17/1/7.
  */
trait UTaskRoute extends SprayJsonSupport with UTaskService with UTaskProtocol {

  /**
    * 1. 查询任务列表    guid为可选参数, 如果没有就是查询所有用户, flowType可选, 没有就是查询所有流程类型
    * GET /utask? guid=:guid& flowType=:flowType& page=:page& pageSize=:pageSize
    */
  def listTask = get {
    path("utask") {
      parameters("guid".?, "flowType".?, "flowId".? ,"page".as[Int].?, "pageSize".as[Int].?) {(guid, flowType, flowId, page, pageSize) =>
        complete(taskList(guid,flowType,flowId,page,pageSize))
      }
    }
  }

  /**
    * 2. 查询指定任务
    * GET /utask/:guid
    */
  def queryTask = get {
    path("utask" / Segment) { guid =>
      complete(utaskQuery(guid))
    }
  }

  /**
    * 3. 提交任务
    * POST /utask/:taskId
    */
  def submitTask = post {
    (path("utask" / Segment / Segment / Segment) & entity(as[UserSubmitEntity])) {  (guid, taskId, flowType, entity) =>


      complete(utaskSubmit(guid,taskId,flowType,entity))
    }
  }

  def utaskRoute: Route = listTask ~ queryTask ~ submitTask
}
