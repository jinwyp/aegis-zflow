package com.yimei.zflow.engine.auto

import com.yimei.zflow.api.models.auto.CommandAutoTask
import com.yimei.zflow.api.models.flow.{State => FlowState}
import com.yimei.zflow.engine.FlowRegistry

/**
  * Created by hary on 17/1/6.
  */
trait AutoService {

  def autoTask(state: FlowState, flowType: String, actorName: String) =
    if (FlowRegistry.auto != null) {
      FlowRegistry.auto ! CommandAutoTask(state, flowType, actorName)
    }
}
