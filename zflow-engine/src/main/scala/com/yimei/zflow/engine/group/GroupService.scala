package com.yimei.zflow.engine.group

import akka.actor.ActorRef
import akka.pattern._
import akka.util.Timeout
import com.yimei.zflow.api.models.group.{State => GroupState, _}

import scala.concurrent.Future

/**
  * Created by hary on 17/1/6.
  */
trait GroupService {

  def proxy: ActorRef

  def groupServiceTimeout: Timeout

  def groupCreate(userType: String, gid: String): Future[GroupState] =
    (proxy ? CommandCreateGroup(s"${userType}!${gid}")) (groupServiceTimeout).mapTo[GroupState]

  def groupQuery(userType: String, gid: String) =
    (proxy ? CommandQueryGroup(s"${userType}!${gid}")) (groupServiceTimeout).mapTo[GroupState]

  def groupClaim(userType: String, gid: String, userId: String, taskId: String): Future[GroupState] =
    (proxy ? CommandClaimTask(s"${userType}!${gid}", taskId, userId)) (groupServiceTimeout).mapTo[GroupState]

  def groupTask(proxy: ActorRef, userType: String, gid: String, flowId: String, taskName: String, flowType: String): Unit =
    proxy ! CommandGroupTask(flowType, flowId, s"${userType}!${gid}", taskName)

}
