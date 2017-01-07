package com.yimei.zflow.util.organ.db

import java.sql.Timestamp

/**
  * Created by hary on 17/1/6.
  */

object Entities {

  case class PartyClassEntity(id: Option[Long], class_name: String, description: String)

  case class PartyGroupEntity(id: Option[Long], party_class: String, gid: String, description: String, ts_c: Timestamp)

  case class PartyInstanceEntity(id: Option[Long], partyClass: String, instanceId: String, companyName: String, disable: Int, ts_c: Timestamp)

  case class PartyUserEntity(id: Option[Long], party_id: Long, user_id: String, password: String, phone: Option[String], email: Option[String], name: String, username: String, disable: Int, ts_c: Timestamp)

  case class UserGroupEntity(id: Option[Long], party_id: Long, gid: String, user_id: String, ts_c: Timestamp)
}
