package com.yimei.zflow.money.utask

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by hary on 17/1/10.
  */
class TaskSwearTest extends WordSpec  with Matchers with ScalatestRouteTest with TaskSwear {

  "TaskSwearTest" should {

    "getSwear" in {

      Get("/task/Swear") ~> getSwear ~> check {
        responseAs[String] shouldBe "get task/Swear"
      }

    }

    "postSwear" in {
      Post("/task/Swear") ~> postSwear ~> check {
        responseAs[String] shouldBe "post task/Swear"
      }

    }

  }
}
