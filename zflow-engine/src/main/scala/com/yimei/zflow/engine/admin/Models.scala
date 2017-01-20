package com.yimei.zflow.engine.admin

import java.sql.Timestamp

import com.yimei.zflow.util.CommonJsonFormat
import spray.json.DefaultJsonProtocol


/**
  * Created by hary on 17/1/7.
  */
object Models extends DefaultJsonProtocol with CommonJsonFormat {

  // deploy
  case class SaveDeploy(id: Option[Long], flow_type: String, jar: String, enable: Boolean, ts_c: Option[Timestamp])
  implicit val saveDeployFormat = jsonFormat5(SaveDeploy)

  // editor
  case class SaveEditor(id: Option[Long], name: String, json: Option[String], meta: Option[String])
  implicit val addDesignFormat = jsonFormat4(SaveEditor)

  case class EditorDetail(name: String, json: String, meta: String, ts_c: Timestamp)
  implicit val designDetailFormat = jsonFormat4(EditorDetail)

  case class EditorModel(name: String, ts_c: Timestamp)
  implicit val designListFormat = jsonFormat2(EditorModel)
}
