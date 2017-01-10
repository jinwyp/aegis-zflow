package com.yimei.zflow.util.asset.routes

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.config.ConfigFactory
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by hary on 17/1/10.
  */
class AssetRouteTest extends {
  override implicit val coreSystem: ActorSystem = ActorSystem("TestSystem", ConfigFactory.parseString(
    """
      |database {
      |  jdbcUrl = "jdbc:mysql://127.0.0.1/cyflow?useUnicode=true&characterEncoding=utf8"
      |  username = "mysql"
      |  password = "mysql"
      |}
      |file.root = "/tmp"
    """.stripMargin))
} with WordSpec with Matchers with ScalatestRouteTest with AssetRoute {
  override val log: LoggingAdapter = Logging(coreSystem, getClass)

//  "AssetRouteTest" should {
//    "downloadFile should xxx" in {
//      Get("/asset/asset_id") ~> assetRoute ~> check {
//        responseAs[String] shouldEqual "hello"
//      }
//    }
//
//    "uploadFile should xxx" in {
//      Post("/asset") ~> assetRoute ~> check {
//        responseAs[String] shouldEqual "kkkk"
//      }
//    }
//
//    "helllo should xxx" in {
//      Get("/kkkk") ~> assetRoute ~> check {
//        responseAs[String] shouldEqual "kernel"
//      }
//    }
//  }
}
