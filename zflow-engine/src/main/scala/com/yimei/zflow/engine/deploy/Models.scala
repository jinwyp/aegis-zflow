package com.yimei.zflow.engine.deploy

import java.sql.Timestamp

import com.yimei.zflow.util.CommonJsonFormat
import spray.json.DefaultJsonProtocol


/**
  * Created by hary on 17/1/7.
  */
object Models extends DefaultJsonProtocol with CommonJsonFormat {

  case class SaveDeploy(id: Option[Long], flow_type: String, jar: String, enable: Boolean, ts_c: Option[Timestamp])
  implicit val saveDeployFormat = jsonFormat5(SaveDeploy)
}
