package com.yimei.zflow.engine.gtask

import akka.pattern._
import akka.util.Timeout
import com.yimei.zflow.api.models.group.{State => GroupState, _}

import scala.concurrent.Future

/**
  * Created by hary on 17/1/6.
  */
trait GTaskService {

  import com.yimei.zflow.engine.FlowRegistry._

  val gtaskTimeout: Timeout

  import scala.concurrent.ExecutionContext.Implicits.global

  def gtaskCreate(ggid: String): Future[GroupState] =
    if (gtask != null) {
      (gtask ? CommandCreateGroup(ggid)) (gtaskTimeout).mapTo[GroupState]
    } else {
      Future {
        throw new Exception("gtask not prepared")
      }
    }

  def gtaskQuery(ggid: String) =
    if (gtask != null) {
      (gtask ? CommandQueryGroup(ggid)) (gtaskTimeout).mapTo[GroupState]
    } else {
      Future {
        throw new Exception("gtask not prepared")
      }
    }

  def gtaskClaim(ggid: String, guid: String, taskId: String): Future[GroupState] =
    if (gtask != null) {
      (gtask ? CommandClaimTask(ggid, taskId, guid)) (gtaskTimeout).mapTo[GroupState]
    } else {
      Future {
        throw new Exception("gtask not prepared")
      }
    }

  def gtaskSend(ggid: String, flowId: String, taskName: String, flowType: String): Unit =
    if (gtask != null) {
      gtask ! CommandGroupTask(flowType, flowId, ggid, taskName)
    } else {
      Future {
        throw new Exception("gtask not prepared")
      }
    }

}
