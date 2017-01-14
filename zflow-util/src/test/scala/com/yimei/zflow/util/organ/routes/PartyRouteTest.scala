package com.yimei.zflow.util.organ.routes

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.config.ConfigFactory
import com.yimei.zflow.util.FlywayDB
import com.yimei.zflow.util.organ.routes.Models._
import com.yimei.zflow.util.HttpResult._
import org.scalatest.{Inside, Matchers, WordSpec}


object Route extends {
  override implicit val coreSystem = ActorSystem("RouteTest", ConfigFactory.parseString(
    """
      |database {
      |  jdbcUrl = "jdbc:mysql://127.0.0.1/cyflow?useUnicode=true&characterEncoding=utf8"
      |  username = "mysql"
      |  password = "mysql"
      |}
    """.stripMargin))
} with PartyRoute {
  override val log: LoggingAdapter = Logging(coreSystem, this.getClass)

  def prepare() = {
    val config = coreSystem.settings.config
    val jdbcUrl = config.getString("database.jdbcUrl")
    val username = config.getString("database.username")
    val password = config.getString("database.password")

    val flyway = new FlywayDB(jdbcUrl, username, password)
    flyway.drop()
    flyway.migrate()
  }
}

/**
  * Created by hary on 17/1/14.
  */
class PartyRouteTest extends
  WordSpec with Matchers with ScalatestRouteTest with Inside with SprayJsonSupport {

  override protected def beforeAll(): Unit = Route.prepare()

  /**
    * 创建参与方类别
    * POST /party/:className/:description
    */

  "参与方路由测试" should {
    "创建参与方类别" in {
      Post("/party/supervisor/description") ~> Route.partyCreate ~> check {
        inside(responseAs[Result[PartyClassEntry]]) {
          case Result(dataOpt, success, None, _) =>
            success shouldBe true
            dataOpt.get.className shouldBe "supervisor"
            dataOpt.get.description shouldBe "description"
        }
      }
    }

    "获取参与方实例" in {
      Get() ~> Route.getParty ~> check {

      }
    }
  }
}
