package com.yimei.zflow.money.utask

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by hary on 17/1/10.
  */
class TaskWriteFriendEvidenceTest extends WordSpec with Matchers with ScalatestRouteTest with TaskWriteFriendEvidence{

  "TaskWriteFriendEvidenceTest" should {

    "getWriteFriendEvidence" in {
      Get("/task/WriteFriendEvidence") ~> getWriteFriendEvidence ~> check {
        responseAs[String] shouldBe "get task/WriteFriendEvidence"
      }
    }

    "postWriteFriendEvidence" in {
      Post("/task/WriteFriendEvidence") ~> postWriteFriendEvidence ~> check {
        responseAs[String] shouldBe "post task/WriteFriendEvidence"
      }
    }

  }
}
