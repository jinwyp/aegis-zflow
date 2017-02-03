package com.yimei.zflow.engine.utask

import java.sql.Timestamp

import com.yimei.zflow.api.models.flow.DataPoint
import com.yimei.zflow.api.models.utask.UTaskProtocol
import com.yimei.zflow.util.CommonJsonFormat
import spray.json.DefaultJsonProtocol

/**
  * Created by wangqi on 17/1/20.
  */
object Models extends UTaskProtocol with DefaultJsonProtocol with CommonJsonFormat{
  case class FlowTaskEntry(flowId: String, flowType: String, taskId: String, taskName: String, taskSubmit: String, guid: String, ts_c: Option[Timestamp])
  implicit val flowTaskEntryFormat = jsonFormat7(FlowTaskEntry)

  case class ListTaskResponse(tasks:Seq[FlowTaskEntry], total:Int)
  implicit val listTaskResponseFormat = jsonFormat2(ListTaskResponse)

  case class UserSubmitEntity(flowId:String,taskName:String,points:Map[String,DataPoint])
  implicit val userSubmitEntityFormat = jsonFormat3(UserSubmitEntity)

}
