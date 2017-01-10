package com.yimei.zflow.util.asset.routes

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.config.ConfigFactory
import com.yimei.zflow.util.FlywayDB
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by hary on 17/1/10.
  */
class AssetRouteTest extends WordSpec with Matchers with ScalatestRouteTest {

  var route: AssetRoute = null;

  override protected def beforeAll(): Unit = {
    super.beforeAll()

    val system: ActorSystem = ActorSystem("TestSystem", ConfigFactory.parseString(
      """
        |database {
        |  jdbcUrl = "jdbc:mysql://127.0.0.1/cyflow?useUnicode=true&characterEncoding=utf8"
        |  username = "mysql"
        |  password = "mysql"
        |}
        |file.root = "/tmp"
      """.stripMargin))

    val config = system.settings.config
    val jdbcUrl = config.getString("database.jdbcUrl")
    val username = config.getString("database.username")
    val password = config.getString("database.password")
    val flyway = new FlywayDB(jdbcUrl, username, password);
    flyway.drop()
    flyway.migrate()

    route = new {
      override implicit val coreSystem = system
      override val log: LoggingAdapter = Logging(coreSystem, getClass)
    } with AssetRoute
  }

  "AssetRouteTest" should {
    "downloadFile should xxx" in {
      Get("/asset/asset_id") ~> route.assetRoute ~> check {
        responseAs[String] shouldEqual "hello"
      }
    }

    "uploadFile should xxx" in {
      Post("/asset") ~> route.assetRoute ~> check {
        responseAs[String] shouldEqual "kkkk"
      }
    }

    "helllo should xxx" in {
      Get("/kkkk") ~> route.assetRoute ~> check {
        responseAs[String] shouldEqual "kernel"
      }
    }
  }

}

