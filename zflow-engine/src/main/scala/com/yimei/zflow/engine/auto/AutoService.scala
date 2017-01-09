package com.yimei.zflow.engine.auto

import akka.actor.ActorRef
import com.yimei.zflow.api.models.auto.CommandAutoTask
import com.yimei.zflow.api.models.flow.{State  => FlowState}

/**
  * Created by hary on 17/1/6.
  */
trait AutoService {
  def auto: ActorRef = ???

  def autoTask(state: FlowState, flowType: String, actorName: String) =
    auto ! CommandAutoTask(state, flowType, actorName)
}
