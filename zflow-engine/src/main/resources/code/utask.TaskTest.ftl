package ${meta.groupId()}.${meta.artifact()}.utask

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

class Task${utask}Test extends WordSpec  with Matchers with ScalatestRouteTest with Task${utask} {

  "Task${utask}Test" should {

    "getAssignFriend" in {
      Get("/task/${utask}") ~> get${utask} ~> check {
        responseAs[String] shouldBe "get task/${utask}"
      }
    }

    "post${utask}" in {
      Post("/task/${utask}") ~> post${utask} ~> check {
        responseAs[String] shouldBe "post task/${utask}"
      }
    }
  }

}
