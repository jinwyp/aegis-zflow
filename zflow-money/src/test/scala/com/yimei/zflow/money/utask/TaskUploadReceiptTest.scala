package com.yimei.zflow.money.utask

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by hary on 17/1/10.
  */
class TaskUploadReceiptTest extends WordSpec  with Matchers with ScalatestRouteTest with TaskUploadReceipt {

  "TaskUploadReceiptTest" should {

    "getUploadReceipt" in {
      Get("/task/UploadReceipt") ~> getUploadReceipt ~> check {
        responseAs[String] shouldBe "get task/UploadReceipt"
      }
    }

    "postUploadReceipt" in {
      Post("/task/UploadReceipt") ~> postUploadReceipt ~> check {
        responseAs[String] shouldBe "post task/UploadReceipt"
      }
    }

  }
}
