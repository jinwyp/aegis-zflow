package com.yimei.zflow.util.organ.routes

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.yimei.zflow.util.{DB, FlywayDB}
import org.scalatest.{Inside, Matchers, WordSpec}

/**
  * Created by hary on 17/1/14.
  */
abstract class RouteTest extends {
  override implicit val coreSystem: ActorSystem = ActorSystem("RouteTest")
} with WordSpec with Matchers with ScalatestRouteTest with Inside with DB {
  override val log: LoggingAdapter = Logging(coreSystem, this.getClass)


  override protected def beforeAll(): Unit = {
    super.beforeAll()
    val config = coreSystem.settings.config
    val jdbcUrl = config.getString("database.jdbcUrl")
    val username = config.getString("database.username")
    val password = config.getString("database.password")

    val flyway = new FlywayDB(jdbcUrl, username, password)
    flyway.drop()
    flyway.migrate()
  }

}
