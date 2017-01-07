package com.yimei.zflow.single

import java.util.UUID

import akka.actor.{ActorRef, Props, Terminated}
import com.yimei.zflow.util.module.{ModuleMaster, ServicableBehavior}
import com.yimei.zflow.api.GlobalConfig._
import com.yimei.zflow.util.id.IdBufferable

import scala.concurrent.duration._
import akka.util.Timeout
import com.yimei.zflow.api.models.flow.{Command, CommandCreateFlow, CommandRunFlow}
import com.yimei.zflow.engine.FlowRegistry
import com.yimei.zflow.engine.flow.{MemoryFlow, PersistentFlow}

/**
  * Created by hary on 16/12/1.
  */
object FlowMaster {
  def props(dependOn: Array[String], persist: Boolean = true)(jdbcUrl: String, username: String, password: String): Props =
    Props(new FlowMaster(dependOn, persist)(jdbcUrl, username, password))
}

/**
  *
  * @param dependOn dependent modules' names
  * @param persist  use persist actor or not
  */
class FlowMaster(dependOn: Array[String], persist: Boolean = true)(jdbcUrl: String, username: String, password: String)
  extends ModuleMaster(module_flow, dependOn)
    with ServicableBehavior
    with IdBufferable {

  // IdBufferable need this
  override val bufferSize: Int = 100
  override val bufferKey: String = "flow"

  override def myIdGenerator = modules(module_id)

  implicit val myEc = context.system.dispatcher
  implicit val myTimeout = Timeout(3 seconds)

  def serving: Receive = {

    // create and run flow
    case command@CommandCreateFlow(flowType, guid, initData) =>

      if (true) {
        val pid = nextId
        val flowId = s"${flowType}!${guid}!${pid}" // 创建flowId
        val child = create(flowId, initData)
        child forward CommandRunFlow(flowId)

      } else {
        // use UUID to generate persistenceId
        val flowId = s"${flowType}!${guid}!${UUID.randomUUID().toString}" // 创建flowId
        val child = create(flowId, initData)
        child forward CommandRunFlow(flowId)
      }

    // other command
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


        val graph = FlowRegistry.flowGraph(flowType)
        if (persist) {
          log.info(s"创建persistent flow..........")
          PersistentFlow.props(graph, flowId, modules, persistenceId, guid, initData)(jdbcUrl, username, password)
        } else {
          log.info(s"创建non-persistent flow..........")
          MemoryFlow.props(graph, flowId, modules, guid, initData)
        }
    }

    context.actorOf(p, flowId)
  }

}


