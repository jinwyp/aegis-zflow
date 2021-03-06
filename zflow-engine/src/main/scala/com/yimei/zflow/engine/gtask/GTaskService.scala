package com.yimei.zflow.engine.gtask

import akka.pattern._
import akka.util.Timeout
import com.yimei.zflow.api.models.gtask.{State => GroupState, _}
import com.yimei.zflow.util.config.Core

import scala.concurrent.Future

/**
  * Created by hary on 17/1/6.
  */
trait GTaskService extends Core{

  import com.yimei.zflow.engine.FlowRegistry._

  val gtaskTimeout: Timeout
  import coreSystem.dispatcher

  def gtaskCreate(ggid: String): Future[GroupState] =
    if (gtask != null) {
      (gtask ? CommandCreateGroup(ggid)) (gtaskTimeout).mapTo[GroupState]
    } else {
      Future.failed(new Exception("gtask is not prepared"))
    }

  def gtaskQuery(ggid: String , flowType: String): Future[GroupState] =
    if (gtask != null) {
      val gState: Future[GroupState] = (gtask ? CommandQueryGroup(ggid)) (gtaskTimeout).mapTo[GroupState]
      gState map { gs =>
        gs.copy(tasks = gs.tasks.filter(entity => entity._2.flowType == flowType))
      }
    } else {
      Future.failed(new Exception("gtask is not prepared"))
    }

  def gtaskClaim(ggid: String, guid: String, taskId: String): Future[GroupState] =
    if (gtask != null) {
      (gtask ? CommandClaimTask(ggid, taskId, guid)) (gtaskTimeout).mapTo[GroupState]
    } else {
      Future.failed(new Exception("gtask is not prepared"))
    }

  def gtaskSend(ggid: String, flowId: String, taskName: String, flowType: String): Unit =
    if (gtask != null) {
      gtask ! CommandGroupTask(flowType, flowId, ggid, taskName)
    } else {
      Future.failed(new Exception("gtask is not prepared"))
    }

}
