package com.yimei.zflow.engine.flow

import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.Timeout
import com.yimei.zflow.api.models.flow.{DataPoint, Graph, State}

import scala.concurrent.duration._
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.Future

/**
  * Created by hary on 17/1/10.
  */
class FlowRouteTest extends WordSpec with Matchers with ScalatestRouteTest with FlowRoute {

  // mock service
  override def flowCreate(guid: String, flowType: String, init: Map[String, String]): Future[State] = super.flowCreate(guid, flowType, init)
  override def flowGraph(flowId: String): Future[Graph] = super.flowGraph(flowId)
  override def flowState(flowId: String): Future[State] = super.flowState(flowId)
  override def flowUpdatePoints(flowId: String, updatePoint: Map[String, String], trigger: Boolean): Future[State] = super.flowUpdatePoints(flowId, updatePoint, trigger)
  override def flowHijack(flowId: String, updatePoints: Map[String, DataPoint], decision: Option[String], trigger: Boolean): Future[State] = super.flowHijack(flowId, updatePoints, decision, trigger)
  override val flowServiceTimeout: Timeout = Timeout(1.seconds)

  "FlowRoute" should {
    "query flow" in {
      Get("/flow/k") ~> flowRoute ~> check {
        responseAs[String] shouldEqual "queryFlow: flowId = k"
      }
    }

    "create flow should" in {
      Post("/flow?guid=guid&flowType=flowType") ~> flowRoute ~> check {
        responseAs[String] shouldEqual "createFlow: guid = guid, flowType = flowType"
      }

//      Post("/flow?guid=guid&flowType=flowType", RequestModel) ~> flowRoute ~>check {
//        responseAs[ResponseModel] shouldEqual ResponseModel(1, 2, 3)
//      }

    }

    "list flow should" in {
      Get("/flow?guid=guid&flowType=flowType&status=status&page=1&pageSize=2")  ~> flowRoute ~> check {
        responseAs[String] shouldEqual "listFlow: guid=guid, flowType=flowType, status=status, page=1, pageSize=2"
      }
    }

    "hijack flow" in {
      Put("/flow/k") ~> flowRoute ~> check {
        responseAs[String] shouldEqual("hijackFlow: flowId = k")
      }
    }
  }


}
