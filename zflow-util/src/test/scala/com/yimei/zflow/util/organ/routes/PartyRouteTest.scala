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


object UT extends PartyRoute {

  override protected def mkSystem: ActorSystem = ActorSystem("RouteTest", ConfigFactory.parseString(
    """
      |database {
      |  jdbcUrl = "jdbc:mysql://127.0.0.1/cyflow?useUnicode=true&characterEncoding=utf8"
      |  username = "mysql"
      |  password = "mysql"
      |  flyway-schema = "schema"
      |}
    """.stripMargin))

  override val log: LoggingAdapter = Logging(coreSystem, this.getClass)
  def prepare() = FlywayDB(coreSystem).drop.migrate
}

/**
  * Created by hary on 17/1/14.
  */
class PartyRouteTest extends
  WordSpec with Matchers with ScalatestRouteTest with Inside with SprayJsonSupport {

  override protected def beforeAll(): Unit = UT.prepare()

  /**
    * 创建参与方类别
    * POST /party/:className/:description
    */

  "参与方路由测试" should {
    "创建参与方类别" in {
      Post("/party/supervisor/description") ~> UT.partyCreate ~> check {
        inside(responseAs[Result[PartyClassEntry]]) {
          case Result(dataOpt, success, None, _) =>
            success shouldBe true
            dataOpt.get.className shouldBe "supervisor"
            dataOpt.get.description shouldBe "description"
        }
      }
    }

    "获取参与方实例" in {
      Get() ~> UT.getParty ~> check {

      }
    }
  }
}
