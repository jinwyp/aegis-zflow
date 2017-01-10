package com.yimei.zflow.engine.gtask

import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.Timeout
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._

/**
  * Created by hary on 17/1/10.
  */
class GTaskRouteTest extends WordSpec with Matchers with ScalatestRouteTest with GTaskRoute {

  // 这里覆盖掉Service里哪些方法, 相当于mock service
  override def gtaskCreate(ggid: String) = ???
  override def gtaskQuery(ggid: String) = ???
  override def gtaskClaim(ggid: String, guid: String, taskId: String) = ???
  override def gtaskSend(ggid: String, flowId: String, taskName: String, flowType: String) = ???
  override val gtaskTimeout: Timeout = Timeout(2 seconds)

  "GTaskRouteTest" should {
    "listTask" in {
      Get("/gtask?ggid=ggid&flowType=flowType&page=1&pageSize=10") ~> gtaskRoute ~> check {
        responseAs[String] shouldEqual "ggid = ggid, flowType = flowType, page = 1, pageSize = 10"
      }
    }

    "claimTask" in {
      Get("/gtask/taskId") ~> gtaskRoute ~> check {
        responseAs[String] shouldEqual "taskId = taskId"
      }
    }
  }
}
