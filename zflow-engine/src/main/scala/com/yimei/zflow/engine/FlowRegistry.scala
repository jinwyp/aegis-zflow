package com.yimei.zflow.engine

import akka.actor.ActorRef
import com.yimei.zflow.api.models.auto.CommandAutoTask
import com.yimei.zflow.api.models.flow.State
import com.yimei.zflow.api.models.group.CommandGroupTask
import com.yimei.zflow.api.models.user.CommandUserTask
import com.yimei.zflow.engine.graph.FlowGraph

/**
  * Created by hary on 16/12/7.
  */
object FlowRegistry {

  /**
    * @param actorName
    * @param state           current state of the flow
    * @param autoMaster      auto service master
    * @param refetchIfExists should refetch if exists
    */
  def fetch(flowType: String, actorName: String, state: State, autoMaster: ActorRef, refetchIfExists: Boolean = false) = {
    if (refetchIfExists ||
      FlowRegistry.registries(flowType).autoTasks(actorName).points.filter(!state.points.filter(t => (!t._2.used)).contains(_)).length > 0
    ) {
      autoMaster ! CommandAutoTask(state, flowType, actorName)
    }
  }

  def gfetch(flowType:String, taskName: String, state: State, groupMaster: ActorRef, ggid:String, refetchIfExists: Boolean = false) = {
    if (refetchIfExists ||
      registries(flowType).userTasks(taskName).points.filter(!state.points.contains(_)).length > 0
    ) {
      // println(s"gfetch with${ggid}, ${state.guid}, ${state}")
      groupMaster ! CommandGroupTask(flowType, state.flowId, ggid, taskName)
    }
  }


  def ufetch(flowType:String, taskName: String, state: State, userMaster: ActorRef, guid:String ,refetchIfExists: Boolean = false) = {
    if (refetchIfExists ||
      registries(flowType).userTasks(taskName).points.filter(!state.points.filter(t=>(!t._2.used)).contains(_)).length > 0
    ) {
      // println(s"ufetch with ${taskName}, ${state.guid}, ${state}")
      userMaster ! CommandUserTask(state.flowId, guid, taskName,flowType)
    }
  }


  var registries = Map[String, FlowGraph]()

  def register(flowType: String, graph: FlowGraph) = {
    if( registries.contains(flowType)) {
      false
    } else {
      registries += (flowType -> graph)
      true
    }
  }

  def flowGraph(flowType: String) = registries(flowType)

}
