package com.yimei.zflow.api.models.database

import java.sql.{Blob, Timestamp}

import spray.json.DefaultJsonProtocol

/**
  * Created by xl on 16/12/27.
  */
object FlowDBModel extends DefaultJsonProtocol {
  case class FlowInstanceEntity(id: Option[Long], flow_id: String, flow_type: String, user_type: String, user_id: String, data: String, state:String, finished: Int, ts_c: Timestamp)
  case class FlowTaskEntity(id: Option[Long], flow_id: String, task_id: String, task_name: String, task_submit: String, user_type: String, user_id: String, ts_c: Timestamp)
  case class DesignEntity(id: Option[Long], name: String, json: Option[String], meta: Option[String], ts_c: Option[Timestamp])
  case class DeployEntity(id: Option[Long], flow_type: String, jar: Blob, enable: Boolean, ts_c: Option[Timestamp])
}


object UserOrganizationDBModel {

}


