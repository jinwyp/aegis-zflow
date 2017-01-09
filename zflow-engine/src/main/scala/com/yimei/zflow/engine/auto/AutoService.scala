package com.yimei.zflow.engine.auto

import com.yimei.zflow.api.models.auto.CommandAutoTask
import com.yimei.zflow.api.models.flow.{State => FlowState}
import com.yimei.zflow.engine.ActorService

/**
  * Created by hary on 17/1/6.
  */
trait AutoService extends ActorService {
  def autoTask(state: FlowState, flowType: String, actorName: String) =
    proxy ! CommandAutoTask(state, flowType, actorName)
}
