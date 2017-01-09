package com.yimei.zflow.engine.gtask

import akka.actor.ActorRef
import akka.pattern._
import akka.util.Timeout
import com.yimei.zflow.api.models.group.{State => GroupState, _}

import scala.concurrent.Future

/**
  * Created by hary on 17/1/6.
  */
trait GTaskService {

  def gtask: ActorRef = ??? // todo

  def gtaskTimeout: Timeout = ??? // todo

  def gtaskCreate(ggid: String): Future[GroupState] =
    (gtask ? CommandCreateGroup(ggid)) (gtaskTimeout).mapTo[GroupState]

  def gtaskQuery(ggid: String) =
    (gtask ? CommandQueryGroup(ggid))(gtaskTimeout).mapTo[GroupState]

  def gtaskClaim(ggid: String, guid: String, taskId: String): Future[GroupState] =
    (gtask ? CommandClaimTask(ggid, taskId, guid))(gtaskTimeout).mapTo[GroupState]

  def gtaskSend(ggid: String, flowId: String, taskName: String, flowType: String): Unit =
    gtask ! CommandGroupTask(flowType, flowId, ggid, taskName)

}
