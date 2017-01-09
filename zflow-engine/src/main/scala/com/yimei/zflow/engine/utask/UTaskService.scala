package com.yimei.zflow.engine.utask

import akka.pattern._
import akka.util.Timeout
import com.yimei.zflow.api.models.flow.DataPoint
import com.yimei.zflow.api.models.user.{CommandCreateUser, CommandQueryUser, CommandTaskSubmit, State => UserState}
import com.yimei.zflow.engine.ActorService

import scala.concurrent.Future

/**
  * Created by hary on 17/1/6.
  */
trait UTaskService extends ActorService {

  def utaskTimeout: Timeout

  // 1> 创建用户
  // 2> 查询用户
  // 3> 用户提交任务
  def utaskCreate(guid: String): Future[UserState] =
    (proxy ? CommandCreateUser(guid)) (utaskTimeout).mapTo[UserState]

  def utaskQuery(guid: String) =
    (proxy ? CommandQueryUser(guid)) (utaskTimeout).mapTo[UserState]

  def utaskSubmit(guid: String, taskId: String, points: Map[String, DataPoint]) =
    (proxy ? CommandTaskSubmit(guid, taskId, points)) (utaskTimeout).mapTo[UserState]
}
