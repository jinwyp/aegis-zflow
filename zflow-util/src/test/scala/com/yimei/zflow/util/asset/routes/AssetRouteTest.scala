package com.yimei.zflow.util.asset.routes

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by hary on 17/1/10.
  */
class AssetRouteTest extends WordSpec with Matchers with ScalatestRouteTest with AssetRoute {
  override def log: LoggingAdapter = null
  override implicit val coreSystem: ActorSystem = null

  "AssetRouteTest" should {
    "downloadFile" in {
      Get("/asset/asset_id") ~> assetRoute ~> check {
        responseAs[String] shouldEqual "hello"
      }
    }

    "uploadFile" in {
      Post("/asset") ~> assetRoute ~> check {
        responseAs[String] shouldEqual "kkkk"
      }
    }

    "helllo" in {
      Get("/kkkk") ~> assetRoute ~> check {
        responseAs[String] shouldEqual "kernel"
      }
    }
  }
}
