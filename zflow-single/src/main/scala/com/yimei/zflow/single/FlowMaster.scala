package com.yimei.zflow.single

import akka.actor.{ActorRef, Props, Terminated}
import com.yimei.zflow.api.GlobalConfig._
import com.yimei.zflow.api.models.flow.Command
import com.yimei.zflow.engine.flow.{MemoryFlow, PersistentFlow}
import com.yimei.zflow.util.module.{ModuleMaster, ServicableBehavior}

/**
  * Created by hary on 16/12/1.
  */
object FlowMaster {
  def props(dependOn: Array[String], persist: Boolean = true): Props =
    Props(new FlowMaster(dependOn, persist))
}

/**
  *
  * @param dependOn dependent modules' names
  * @param persist  use persist actor or not
  */
class FlowMaster(dependOn: Array[String], persist: Boolean = true)
  extends ModuleMaster(module_flow, dependOn)
    with ServicableBehavior {

  def serving: Receive = {

    case command: Command =>
      log.debug(s"get command $command and forward to child!!!!")
      val child = context.child(command.flowId).fold(create(command.flowId))(identity)
      child forward command

    case Terminated(child) =>
      log.info(s"${child.path.name} terminated")
  }

  /**
    * 创建
    *
    * @param flowId
    * @return
    */
  def create(flowId: String, initData: Map[String, String] = Map()): ActorRef = {
    val regex = "([^!]+)!([^!]+![^!]+)!(.*)".r
    val p = flowId match {
      case regex(flowType, guid, persistenceId) =>

        log.info(s"flowId in flowProp is ${flowId}, ${guid}, ${persistenceId}")


        if (persist) {
          log.info(s"创建persistent flow..........")
          PersistentFlow.props(modules)
        } else {
          log.info(s"创建non-persistent flow..........")
          MemoryFlow.props(modules)
        }
    }

    context.actorOf(p, flowId)
  }

}


