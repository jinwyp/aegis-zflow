package ${meta.groupId()}.${meta.artifact()}.utask

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

class Task${code}Test extends WordSpec  with Matchers with ScalatestRouteTest with Task${code} {

  "Task${code}Test" should {

    "getAssignFriend" in {
      Get("/task/${code}") ~> get${code} ~> check {
        responseAs[String] shouldBe "get task/${code}"
      }
    }

    "post${code}" in {
      Post("/task/${code}") ~> post${code} ~> check {
        responseAs[String] shouldBe "post task/${code}"
      }
    }
  }

}
