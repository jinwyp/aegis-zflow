package com.yimei.zflow.engine.updater

import akka.actor.{Actor, ActorSystem, Props}
import akka.event.{Logging, LoggingAdapter}
import com.yimei.zflow.api.models.flow.{FlowProtocol, State}
import com.yimei.zflow.engine.db.FlowInstanceTable

object Updater {

  case class FlowStateUpdate(state: State)
  case class FlowVertexUpdate(state: State, vertex: String)

  def props(system: ActorSystem): Props = Props(new Updater(system))
}

/**
  * Created by hary on 17/1/11.
  */
class Updater(system: ActorSystem) extends {
  override implicit val coreSystem = system
} with Actor with FlowInstanceTable with FlowProtocol {

  override val log: LoggingAdapter = Logging(coreSystem, this.getClass)

  import Updater._
  import driver.api._
  import spray.json._

  override def receive: Receive = {
    case FlowStateUpdate(state: State) =>
      // 更新数据库 todo王琦
      val pu = flowInstance.filter(f =>
        f.flow_id === state.flowId
      ).map(f => (f.data, f.finished)).update(
        state.toJson.toString, 1
      )

      dbrun(pu)

    case FlowVertexUpdate(state, vertex) =>
      //更新当前到达点
      val udState = flowInstance.filter(f=>
        f.flow_id === state.flowId
      ).map(f=>(f.state)).update(
        vertex
      )
      dbrun(udState)
  }
}
