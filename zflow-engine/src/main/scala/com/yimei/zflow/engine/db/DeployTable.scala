package com.yimei.zflow.engine.db

import java.sql.{Blob, Timestamp}

import com.yimei.zflow.engine.db.Entities.DeployEntity
import com.yimei.zflow.util.DB

/**
  * Created by hary on 16/12/28.
  */
trait DeployTable extends DB {

  import driver.api._

  class DeployClass(tag: Tag) extends Table[DeployEntity](tag, "deploy") {
    def id         = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
    def flow_type  = column[String]("flow_type")
    def jar        = column[Blob]("jar")
    def enable     = column[Boolean]("enable")
    def ts_c       = column[Option[Timestamp]]("ts_c")

    def * = (id, flow_type, jar, enable, ts_c) <>(DeployEntity.tupled, DeployEntity.unapply)
  }

  protected val deployClass = TableQuery[DeployClass]

}
