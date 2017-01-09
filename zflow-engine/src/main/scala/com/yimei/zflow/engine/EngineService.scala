package com.yimei.zflow.engine

import com.yimei.zflow.engine.flow.FlowService
import com.yimei.zflow.engine.gtask.GTaskService
import com.yimei.zflow.engine.utask.UTaskService

/**
  * Created by hary on 17/1/6.
  */
trait EngineService extends FlowService with GTaskService with UTaskService
