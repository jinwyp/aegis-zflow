package com.yimei.zflow.engine.db

import java.sql.Timestamp

import com.yimei.zflow.engine.db.Entities.FlowTaskEntity
import com.yimei.zflow.util.DB


/**
  * Created by hary on 16/12/16.
  */

trait FlowTaskTable extends DB {
  import driver.api._

  class FlowTask(tag:Tag) extends Table[FlowTaskEntity](tag,"flow_task"){
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
    def flow_id = column[String]("flow_id")
    def flow_type = column[String]("flow_type")
    def task_id = column[String]("task_id")
    def task_name = column[String]("task_name")
    def task_submit = column[String]("task_submit")
    def guid = column[String]("guid")
    def ts_c = column[Option[Timestamp]]("ts_c")

    def * = (id,flow_id,flow_type,task_id,task_name,task_submit,guid,ts_c) <> (FlowTaskEntity.tupled,FlowTaskEntity.unapply)
  }

  protected val flowTask = TableQuery[FlowTask]
}
