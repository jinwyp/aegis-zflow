package com.yimei.zflow.money.utask

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by hary on 17/1/10.
  */
class TaskAssignFriendTest extends WordSpec  with Matchers with ScalatestRouteTest with TaskAssignFriend {

  "TaskAssignFriendTest" should {

    "getAssignFriend" in {
      Get("/task/AssignFriend") ~> getAssignFriend ~> check {
        responseAs[String] shouldBe "get task/AssignFriend"
      }
    }

    "postAssignFriend" in {
      Post("/task/AssignFriend") ~> postAssignFriend ~> check {
        responseAs[String] shouldBe "post task/AssignFriend"
      }
    }

  }
}
