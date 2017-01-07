package com.yimei.zflow.util.organ.db

import java.sql.Timestamp

import com.yimei.zflow.util.DB
import com.yimei.zflow.util.organ.db.Entities.PartyInstanceEntity

/**
  * Created by hary on 16/12/16.
  */


trait PartyInstanceTable extends DB {

  import driver.api._

  class PartyInstance(tag: Tag) extends Table[PartyInstanceEntity](tag, "party_instance") {
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)

    def party_class = column[String]("party_class")

    def instance_id = column[String]("instance_id")

    def party_name = column[String]("party_name")

    def disable = column[Int]("disable")

    def ts_c = column[Timestamp]("ts_c")

    def * = (id, party_class, instance_id, party_name, disable, ts_c) <>(PartyInstanceEntity.tupled, PartyInstanceEntity.unapply)
  }

  protected val partyInstance = TableQuery[PartyInstance]
}
