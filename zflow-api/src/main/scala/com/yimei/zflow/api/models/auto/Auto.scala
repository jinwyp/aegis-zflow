package com.yimei.zflow.api.models.auto

import com.yimei.zflow.api.models.flow.State


/**
  *
  * @param state     State
  * @param actorName actorName
  */
case class CommandAutoTask(state: State, flowType: String, actorName: String)

