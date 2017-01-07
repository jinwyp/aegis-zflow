package com.yimei.zflow.util.organ.db

import java.sql.Timestamp

import com.yimei.zflow.util.DB
import com.yimei.zflow.util.organ.db.Entities.PartyUserEntity

/**
  * Created by hary on 16/12/16.
  */


trait PartyUserTable extends DB {

  import driver.api._

  class PartyUser(tag:Tag) extends Table[PartyUserEntity](tag,"party_user"){
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
    def party_id = column[Long]("party_id")
    def user_id = column[String]("user_id")
    def password = column[String]("password")
    def phone = column[Option[String]]("phone")
    def email = column[Option[String]]("email")
    def name = column[String]("name")
    def username = column[String]("username")
    def disable = column[Int]("disable")
    def ts_c = column[Timestamp]("ts_c")

    def * = (id,party_id,user_id,password,phone,email,name,username,disable,ts_c) <> (PartyUserEntity.tupled,PartyUserEntity.unapply)
  }

  protected val partyUser = TableQuery[PartyUser]
}
