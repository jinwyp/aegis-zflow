package com.yimei.zflow.engine.flow

import akka.actor.{ActorRef, Props}
import akka.event.Logging
import com.yimei.zflow.engine.FlowRegistry

object MemoryFlow {
  def props(modules: Map[String, ActorRef]) = Props(new MemoryFlow(modules))
}

/**
  *
  * @param modules
  */
class MemoryFlow(modules: Map[String, ActorRef]) extends AbstractFlow {

  val log = Logging(context.system.eventStream, this)

  import com.yimei.zflow.api.models.flow._

  val flowId = self.path.name

  // flowType!guid!pid
  val regex = "([^!]+)!([^!]+![^!]+)!(.*)".r
  val (flowType, guid, pid, graph) = flowId match {
    case regex(xflowType, xguid, xpid) => (xflowType, xguid, xpid, FlowRegistry.flowGraph(xflowType))
    case _ => throw new Exception("xxxxx")
  }


  override var state: State = State(flowId, guid, Map(), Map("start" -> true), Nil, graph.flowType)

  override def genGraph(state: State): Graph = graph.graph(state)

  //   override def modules: Map[String, ActorRef] = dependOn

  override def receive: Receive = commonBehavior orElse serving

  // servicable
  val serving: Receive = ???

}
