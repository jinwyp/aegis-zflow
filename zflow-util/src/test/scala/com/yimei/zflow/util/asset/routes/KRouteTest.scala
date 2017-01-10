package com.yimei.zflow.util.asset.routes

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server.Directives._
import org.scalatest.{Matchers, WordSpec}
import spray.json.DefaultJsonProtocol

/**
  * Created by hary on 17/1/10.
  */
class KRouteTest extends WordSpec with Matchers with ScalatestRouteTest with DefaultJsonProtocol with SprayJsonSupport {

  info("this is a test")

  def listTest: Route = get {
    path("hello") {
      complete("hello")
    }
  }

  "KRouteTest" should {

    "listTest" in {
      Get("/hello") ~> listTest ~> check {
        responseAs[String] shouldBe "hello"
      }
    }
  }

}
