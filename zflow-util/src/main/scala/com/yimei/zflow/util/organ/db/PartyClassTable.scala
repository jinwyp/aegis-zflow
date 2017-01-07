package com.yimei.zflow.util.organ.db

import com.yimei.zflow.util.DB
import com.yimei.zflow.util.organ.db.Entities.PartyClassEntity

/**
  * Created by wangqi on 16/12/19.
  */


trait PartyClassTable extends DB {
  import driver.api._

  class PartyClass(tag: Tag) extends Table[PartyClassEntity](tag, "party_class") {
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)

    def class_name = column[String]("class_name")

    def description = column[String]("description")

    def * = (id, class_name, description) <> (PartyClassEntity.tupled, PartyClassEntity.unapply)
  }

  protected val partClass = TableQuery[PartyClass]
}
