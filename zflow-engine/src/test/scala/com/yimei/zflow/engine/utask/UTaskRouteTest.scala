package com.yimei.zflow.engine.utask

import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.Timeout
import com.yimei.zflow.api.models.flow.DataPoint
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._

/**
  * Created by hary on 17/1/10.
  */
class UTaskRouteTest extends WordSpec  with Matchers with ScalatestRouteTest with UTaskRoute {
  override val utaskTimeout: Timeout = Timeout(3 seconds)

  // mock service
  override def utaskCreate(guid: String) = ???
  override def utaskQuery(guid: String) = ???
  override def utaskSubmit(guid: String, taskId: String, points: Map[String, DataPoint]) = ???

  "UTaskRouteTest" should {

    "listTask" in {
      Get("/utask?guid=guid&flowType=flowType&page=1&pageSize=10") ~> utaskRoute ~> check {
        responseAs[String] shouldEqual "listTask: guid = guid, flowType = flowType, page = 1, pageSize = 10"
      }
    }

    "queryTask" in {
      Get("/utask/taskId") ~> utaskRoute ~> check {
        responseAs[String] shouldEqual "queryTask: taskId = taskId"
      }
    }

    "submitTask" in {
      Post("/utask/taskId") ~> utaskRoute ~> check {
        responseAs[String] shouldEqual "submitTask: taskId = taskId"
      }
    }

  }
}
