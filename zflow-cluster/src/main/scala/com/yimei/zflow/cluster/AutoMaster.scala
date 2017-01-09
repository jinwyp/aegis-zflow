package com.yimei.zflow.cluster

import akka.actor.{ActorRef, Props}
import com.yimei.zflow.api.GlobalConfig._
import com.yimei.zflow.api.models.auto.CommandAutoTask
import com.yimei.zflow.engine.FlowRegistry
import com.yimei.zflow.engine.auto.AutoActor
import com.yimei.zflow.util.module.{ModuleMaster, ServicableBehavior}

/**
  * Created by hary on 16/12/3.
  */
object AutoMaster {
  def props(dependOn: Array[String]) = Props(new AutoMaster(dependOn))
}

class AutoMaster(dependOn: Array[String]) extends ModuleMaster(module_auto, dependOn) with ServicableBehavior {

  // all child auto actors flowType -> actorName -> actorRef
  var actors = Map[String, Map[String, ActorRef]]()

  // servicable behavior
  override def serving: Receive = {
    case get@CommandAutoTask(flowId, flowType, actorName) =>
      log.debug(s"forward ${get} to ${actors(flowType)(actorName)} ")
      actors(flowType)(actorName) forward get
  }

  // create all child actors
  override def initHook(): Unit = {
    startActors
  }

  def startActors() = {

    FlowRegistry.registries.foreach { e =>
      val flowType = e._1
      val graph = e._2
      val jar = graph.moduleJar

      for (be <- graph.autoMethods) {

        val actor = context.actorOf(Props(new AutoActor(be._1, modules, be._2)), s"${flowType}.${be._1}")
        if (actors.contains(flowType)) {
          val entry = actors(flowType) + (be._1 -> actor)
          actors = actors + (flowType -> entry)
        } else {
          actors = actors + (flowType -> Map(be._1 -> actor))
        }
      }
    }
  }
}

