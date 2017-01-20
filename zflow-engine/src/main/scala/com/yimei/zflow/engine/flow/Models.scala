package com.yimei.zflow.engine.flow

import java.sql.Timestamp

import com.yimei.zflow.api.models.flow.{DataPoint, FlowProtocol}
import com.yimei.zflow.util.CommonJsonFormat
import spray.json.DefaultJsonProtocol

/**
  * Created by wangqi on 17/1/20.
  */
object Models  extends FlowProtocol with DefaultJsonProtocol with CommonJsonFormat {

  case class FlowInstanceEntry(flowId: String, flowType: String, guid: String, data: String, state:String, finished: Int, ts_c: Option[Timestamp])
  implicit val flowInstanceEntityFormat = jsonFormat7(FlowInstanceEntry)


  case class ListFlowResponse(flows:Seq[FlowInstanceEntry],total:Int)
  implicit val listFlowResponseFormat = jsonFormat2(ListFlowResponse)


  case class HijackEntity(updatePoints: Map[String, DataPoint], decision: Option[String], trigger: Boolean)
  implicit val hijackEntityFormat = jsonFormat3(HijackEntity)

}
