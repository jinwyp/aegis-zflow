package com.yimei.zflow.util

import akka.actor.ActorSystem
import com.typesafe.config.Config
import org.flywaydb.core.Flyway

/**
  * Created by hary on 17/1/6.
  */
class FlywayDB(jdbcUrl: String, username: String, password: String, schema: String = "schema") {

  def this(config: Config) {
    this(
      config.getString("database.jdbcUrl"),
      config.getString("database.username"),
      config.getString("database.password"),
      config.getString("database.flyway-schema")
    )
  }

  def this(system: ActorSystem) {
    this(system.settings.config)
  }

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

object FlywayDB {
  def apply(jdbcUrl: String, username: String, password: String, schema: String = "schema") = new FlywayDB(jdbcUrl, username, password, schema)
  def apply(system: ActorSystem) = new FlywayDB(system)
  def apply(config: Config) = new FlywayDB(config)
}
