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

  def groupCreate(ggid: String): Future[GroupState] =
    (proxy ? CommandCreateGroup(ggid)) (groupServiceTimeout).mapTo[GroupState]

  def groupQuery(ggid: String) =
    (proxy ? CommandQueryGroup(ggid))(groupServiceTimeout).mapTo[GroupState]

  def groupClaim(ggid: String, guid: String, taskId: String): Future[GroupState] =
    (proxy ? CommandClaimTask(ggid, taskId, guid))(groupServiceTimeout).mapTo[GroupState]

  def groupTask(ggid: String, flowId: String, taskName: String, flowType: String): Unit =
    proxy ! CommandGroupTask(flowType, flowId, ggid, taskName)

}
