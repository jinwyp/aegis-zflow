package com.yimei.zflow.util.asset.routes

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.config.ConfigFactory
import com.yimei.zflow.util.FlywayDB
import org.scalatest.{Matchers, WordSpec}

object AssetRouteUT extends {
  implicit val coreSystem: ActorSystem = ActorSystem("TestSystem", ConfigFactory.parseString(
    """
      |database {
      |  jdbcUrl = "jdbc:mysql://127.0.0.1/cyflow?useUnicode=true&characterEncoding=utf8"
      |  username = "mysql"
      |  password = "mysql"
      |}
      |file.root = "/tmp"
      |akka.http.session.server-secret = "1234567891234567891234567891234567891234567890000012345678901234"
      |
    """.stripMargin))
} with AssetRoute {
  val fileField = "file"

  def prepare() = {
    val config = coreSystem.settings.config
    val jdbcUrl = config.getString("database.jdbcUrl")
    val username = config.getString("database.username")
    val password = config.getString("database.password")
    val flyway = new FlywayDB(jdbcUrl, username, password);
    flyway.drop()
    flyway.migrate()
  }
}

/**
  * Created by hary on 17/1/10.
  */
class AssetRouteTest extends WordSpec with Matchers with ScalatestRouteTest {

  import AssetRouteUT.{assetRoute, prepare}

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    prepare
  }

  // must be tested with login session

  "AssetRouteTest" should {
    "downloadFile should xxx" in {
      Get("/asset/asset_id") ~> assetRoute ~> check {
        responseAs[String] shouldEqual "hello"
      }
    }

    "uploadFile should xxx" in {
      Post("/asset") ~> assetRoute ~> check {
        responseAs[String] shouldEqual "kkkk"
      }
    }

    "helllo should xxx" in {
      Get("/kkkk") ~> assetRoute ~> check {
        responseAs[String] shouldEqual "kernel"
      }
    }
  }

}

