package com.yimei.zflow.engine.admin.db

import java.sql.Timestamp

import com.yimei.zflow.engine.admin.db.Entities.EditorEntity
import com.yimei.zflow.util.DB

/**
  * Created by hary on 16/12/28.
  */
trait EditorTable extends DB {

  import driver.api._

  class EditorClass(tag: Tag) extends Table[EditorEntity](tag, "editor") {
    def id        = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
    def name      = column[String]("name")
    def json      = column[Option[String]]("json")
    def meta      = column[Option[String]]("meta")
    def ts_c      = column[Option[Timestamp]]("ts_c")

    def * = (id, name, json, meta, ts_c) <> (EditorEntity.tupled, EditorEntity.unapply)
  }

  protected val editorClass = TableQuery[EditorClass]

}
