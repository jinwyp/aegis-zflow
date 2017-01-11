package com.yimei.zflow.engine.admin.db

import java.sql.{Blob, Timestamp}

/**
  * Created by hary on 17/1/7.
  */
object Entities {
  case class DesignEntity(id: Option[Long], name: String, json: Option[String], meta: Option[String], ts_c: Option[Timestamp])
  case class DeployEntity(id: Option[Long], flow_type: String, jar: Blob, enable: Boolean, ts_c: Option[Timestamp])
}
