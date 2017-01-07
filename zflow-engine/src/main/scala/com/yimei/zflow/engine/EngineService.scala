package com.yimei.zflow.engine

import com.yimei.zflow.engine.flow.FlowService
import com.yimei.zflow.engine.group.GroupService
import com.yimei.zflow.engine.user.UserService

/**
  * Created by hary on 17/1/6.
  */
trait EngineService extends FlowService with GroupService with UserService
