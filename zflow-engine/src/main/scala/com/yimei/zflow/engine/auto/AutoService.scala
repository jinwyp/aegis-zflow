package com.yimei.zflow.engine.auto

import com.yimei.zflow.api.models.auto.CommandAutoTask
import com.yimei.zflow.api.models.flow.{State => FlowState}
import com.yimei.zflow.engine.FlowRegistry
import com.yimei.zflow.engine.db.Entities.FlowInstanceEntity
import com.yimei.zflow.engine.db.FlowInstanceTable
import com.yimei.zflow.engine.flow.FlowService
import com.yimei.zflow.util.config.Core
import com.yimei.zflow.util.exception.DatabaseException

import scala.concurrent.Future

/**
  * Created by hary on 17/1/6.
  */
trait AutoService extends FlowInstanceTable with FlowService with Core {

  import driver.api._
  import coreSystem.dispatcher

  /**
    * 发起自动任务
    *
    * @param flowId
    * @param flowType
    * @param actorName
    * @return
    */
  def autoTask(flowId: String, flowType: String, actorName: String): Future[String] = {
    if (FlowRegistry.auto != null) {
      val flow: Future[FlowInstanceEntity] = dbrun(flowInstance.filter(_.flow_id === flowId).result.head) recover {
        case _ => throw new DatabaseException("该流程不存在")
      }
      for {
        f <- flow
        s <- flowState(f.flow_id)
      } yield {
        FlowRegistry.auto ! CommandAutoTask(s, flowType, actorName)
        "success"
      }

    } else {
      Future.failed(new Exception("auto is not prepared"))
    }
  }


}
