package com.yimei.zflow.util

import java.sql.SQLIntegrityConstraintViolationException

import akka.event.LoggingAdapter
import com.yimei.zflow.util.config.Core
import com.yimei.zflow.util.exception.DatabaseException
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import slick.dbio.NoStream

import scala.concurrent.{ExecutionContext, Future}


/**
  * Created by hary on 17/1/6.
  */
trait DB extends Core {

  def log: LoggingAdapter

  val driver = slick.driver.MySQLDriver;
  import driver.api.Database;

  val db = Database.forDataSource(
    new HikariDataSource({
      val hikariConfig = new HikariConfig()
      hikariConfig.setJdbcUrl(coreSystem.settings.config.getString("database.jdbcUrl"))
      hikariConfig.setUsername(coreSystem.settings.config.getString("database.username"))
      hikariConfig.setPassword(coreSystem.settings.config.getString("database.password"))
      hikariConfig
    })
  )
  implicit val session = db.createSession()
  import slick.dbio.DBIOAction

  //封装数据库操作
  def dbrun[R](a: DBIOAction[R, NoStream, Nothing])(implicit ec: ExecutionContext = coreSystem.dispatcher): Future[R] = {
    val result = db.run(a)
    result onFailure {
      case a: SQLIntegrityConstraintViolationException => log.warning("该记录已存在")
      case a => {
        log.info("database err: {}", a); throw new DatabaseException(a.getMessage)
      }
    }
    result
  }
}
