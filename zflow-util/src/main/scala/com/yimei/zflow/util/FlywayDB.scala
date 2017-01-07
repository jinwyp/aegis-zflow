package com.yimei.zflow.util

import org.flywaydb.core.Flyway

/**
  * Created by hary on 17/1/6.
  */
class FlywayDB(url: String, user: String, password: String, schema: String = "schema") {

  val flyway = new Flyway()
  flyway.setDataSource(url, user, password )
  flyway.setTable(schema)


  def migrate() = {
    flyway.migrate()
    this
  }

  def drop() = {
    flyway.clean()
    this
  }
}
