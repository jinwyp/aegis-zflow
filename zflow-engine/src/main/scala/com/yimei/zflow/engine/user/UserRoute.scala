package com.yimei.zflow.engine.user

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

/**
  * Created by hary on 17/1/7.
  */
trait UserRoute extends UserService with SprayJsonSupport {

  /**
    *
  1. 查询任务列表    guid为可选参数, 如果没有就是查询所有用户, flowType可选, 没有就是查询所有流程类型
  GET /utask?
    guid=:guid&
    flowType=:flowType&
    page=:page&
    pageSize=:pageSize
    */
  def listTask = get {
    complete("ok")
  }

  /**
    *
  2. 查询指定任务
    GET /utask/:taskId
    */
  def queryTask = get {
    complete("ok")
  }

  /**
    *
  3. 提交任务
    POST /utask/:taskId
    */
  def submitTask = post {
    complete("ok")
  }

  def userRoute: Route  = listTask ~ queryTask ~ submitTask
}
