package com.yimei.zflow.engine.db

import java.sql.{Blob, Timestamp}

/**
  * Created by hary on 17/1/7.
  */
object Entities {
  case class FlowInstanceEntity(id: Option[Long], flow_id: String, flow_type: String, user_type: String, user_id: String, data: String, state:String, finished: Int, ts_c: Timestamp)
  case class FlowTaskEntity(id: Option[Long], flow_id: String, task_id: String, task_name: String, task_submit: String, user_type: String, user_id: String, ts_c: Timestamp)
  case class DesignEntity(id: Option[Long], name: String, json: Option[String], meta: Option[String], ts_c: Option[Timestamp])
  case class DeployEntity(id: Option[Long], flow_type: String, jar: Blob, enable: Boolean, ts_c: Option[Timestamp])
}