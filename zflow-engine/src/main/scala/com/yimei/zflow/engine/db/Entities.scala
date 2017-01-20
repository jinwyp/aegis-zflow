package com.yimei.zflow.engine.db

import java.sql.{Blob, Timestamp}

/**
  * Created by hary on 17/1/7.
  */
object Entities {
  case class FlowInstanceEntity(id: Option[Long], flow_id: String, flow_type: String, guid: String, data: String, state:String, finished: Int, ts_c: Option[Timestamp])
  case class FlowTaskEntity(id: Option[Long], flow_id: String, flow_type: String, task_id: String, task_name: String, task_submit: String, guid: String, ts_c: Option[Timestamp])
}
