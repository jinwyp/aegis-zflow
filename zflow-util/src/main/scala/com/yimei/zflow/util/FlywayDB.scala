package com.yimei.zflow.util

import org.flywaydb.core.Flyway

/**
  * Created by hary on 17/1/6.
  */
class FlywayDB(jdbcUrl: String, username: String, password: String, schema: String = "schema") {

  val flyway = new Flyway()
  flyway.setDataSource(jdbcUrl, username, password )
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
