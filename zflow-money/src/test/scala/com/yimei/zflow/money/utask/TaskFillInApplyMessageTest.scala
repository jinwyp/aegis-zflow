package com.yimei.zflow.money.utask

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by hary on 17/1/10.
  */
class TaskFillInApplyMessageTest extends WordSpec  with Matchers with ScalatestRouteTest with TaskFillInApplyMessage {

  "TaskFillInApplyMessageTest" should {

    "getFillInApplyMessage" in {
      Get("/task/FillInApplyMessage") ~> getFillInApplyMessage ~> check {
        responseAs[String] shouldBe "get task/FillInApplyMessage"
      }
    }

    "postFillInApplyMessage" in {
      Post("/task/FillInApplyMessage") ~> postFillInApplyMessage ~> check {
        responseAs[String] shouldBe "post task/FillInApplyMessage"
      }
    }
  }
}
