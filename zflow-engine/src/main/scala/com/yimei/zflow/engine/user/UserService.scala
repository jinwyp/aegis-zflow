package com.yimei.zflow.engine.user

import akka.actor.ActorRef
import akka.util.Timeout
import akka.pattern._
import com.yimei.zflow.api.models.flow.DataPoint
import com.yimei.zflow.api.models.user.{CommandCreateUser, CommandQueryUser, CommandTaskSubmit, State => UserState}

import scala.concurrent.Future

/**
  * Created by hary on 17/1/6.
  */
trait UserService {

  def proxy: ActorRef

  def userServiceTimeout: Timeout

  // 1> 创建用户
  // 2> 查询用户
  // 3> 用户提交任务
  def userCreate(guid: String): Future[UserState] =
    (proxy ? CommandCreateUser(guid))(userServiceTimeout).mapTo[UserState]

  def userQuery(guid: String) =
    (proxy ? CommandQueryUser(guid))(userServiceTimeout).mapTo[UserState]

  def userSubmit(guid: String, taskId: String, points: Map[String, DataPoint]) =
    (proxy ? CommandTaskSubmit(guid, taskId, points))(userServiceTimeout).mapTo[UserState]
}
