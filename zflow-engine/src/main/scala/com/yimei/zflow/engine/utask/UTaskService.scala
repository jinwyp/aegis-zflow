package com.yimei.zflow.engine.utask

import akka.actor.ActorRef
import akka.util.Timeout
import akka.pattern._
import com.yimei.zflow.api.models.flow.DataPoint
import com.yimei.zflow.api.models.user.{CommandCreateUser, CommandQueryUser, CommandTaskSubmit, State => UserState}

import scala.concurrent.Future

/**
  * Created by hary on 17/1/6.
  */
trait UTaskService {

  def utask: ActorRef = ???  // todo

  def utaskTimeout: Timeout = ??? // todo

  // 1> 创建用户
  // 2> 查询用户
  // 3> 用户提交任务
  def utaskCreate(guid: String): Future[UserState] =
    (utask ? CommandCreateUser(guid))(utaskTimeout).mapTo[UserState]

  def utaskQuery(guid: String) =
    (utask ? CommandQueryUser(guid))(utaskTimeout).mapTo[UserState]

  def utaskSubmit(guid: String, taskId: String, points: Map[String, DataPoint]) =
    (utask ? CommandTaskSubmit(guid, taskId, points))(utaskTimeout).mapTo[UserState]
}
