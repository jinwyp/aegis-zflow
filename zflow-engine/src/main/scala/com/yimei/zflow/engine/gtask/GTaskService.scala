package com.yimei.zflow.engine.gtask

import akka.actor.ActorRef
import akka.pattern._
import akka.util.Timeout
import com.yimei.zflow.api.models.group.{State => GroupState, _}
import com.yimei.zflow.engine.ActorService

import scala.concurrent.Future

/**
  * Created by hary on 17/1/6.
  */
trait GTaskService extends ActorService {

  def gtaskTimeout: Timeout = ??? // todo

  def gtaskCreate(ggid: String): Future[GroupState] =
    (proxy ? CommandCreateGroup(ggid)) (gtaskTimeout).mapTo[GroupState]

  def gtaskQuery(ggid: String) =
    (proxy ? CommandQueryGroup(ggid))(gtaskTimeout).mapTo[GroupState]

  def gtaskClaim(ggid: String, guid: String, taskId: String): Future[GroupState] =
    (proxy ? CommandClaimTask(ggid, taskId, guid))(gtaskTimeout).mapTo[GroupState]

  def gtaskSend(ggid: String, flowId: String, taskName: String, flowType: String): Unit =
    proxy ! CommandGroupTask(flowType, flowId, ggid, taskName)

}
