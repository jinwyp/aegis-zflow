package com.yimei.zflow.money.utask

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by hary on 17/1/10.
  */
class TaskWifeApproveTest extends WordSpec  with Matchers with ScalatestRouteTest with TaskWifeApprove {

  "TaskWifeApproveTest" should {

    "getWifeApprove" in {
      Get("/task/WifeApprove") ~> getWifeApprove ~> check {
        responseAs[String] shouldBe "get task/WifeApprove"
      }
    }

    "postWifeApprove" in {
      Post("/task/WifeApprove") ~> postWifeApprove ~> check {
        responseAs[String] shouldBe "post task/WifeApprove"
      }

    }

  }
}
