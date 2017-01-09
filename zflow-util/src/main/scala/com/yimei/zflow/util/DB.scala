package com.yimei.zflow.util

import java.sql.SQLIntegrityConstraintViolationException

import akka.event.LoggingAdapter
import com.yimei.zflow.util.config.CoreConfig
import com.yimei.zflow.util.exception.DatabaseException
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import slick.dbio.NoStream

import scala.concurrent.Future


/**
  * Created by hary on 17/1/6.
  */
trait DB extends CoreConfig {

//  val jdbcUrl: String
//  val username: String
//  val password: String

  val log: LoggingAdapter

  val hikariConfig = new HikariConfig()
  hikariConfig.setJdbcUrl(coreConfig.getString("database.jdbcUrl"))
  hikariConfig.setUsername(coreConfig.getString("database.username"))
  hikariConfig.setPassword(coreConfig.getString("database.password"))

  private val dataSource = new HikariDataSource(hikariConfig)
  val driver = slick.driver.MySQLDriver;
  import driver.api.Database;
  val db = Database.forDataSource(dataSource)
  db.createSession()

  import slick.dbio.DBIOAction

  //封装数据库操作
  def dbrun[R](a: DBIOAction[R, NoStream, Nothing]): Future[R] = {
    val result = db.run(a)
    result onFailure {
      case a:SQLIntegrityConstraintViolationException => log.warning("该记录已存在")
      case a => {log.info("database err: {}",a); throw new DatabaseException(a.getMessage)}
    }
    result
  }
}
