package com.yimei.zflow.cluster

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, SupervisorStrategy, Terminated}
import com.yimei.zflow.api.GlobalConfig._
import com.yimei.zflow.api.models.user.{Command => UserCommand}
import com.yimei.zflow.cluster.flow.FlowProxy
import com.yimei.zflow.cluster.gtask.GTaskProxy
import com.yimei.zflow.cluster.utask.UTaskProxy
import com.yimei.zflow.engine.FlowRegistry
import com.yimei.zflow.engine.updater.Updater
import com.yimei.zflow.util.id.IdGenerator
import com.yimei.zflow.util.module.ModuleMaster.{GiveMeModule, RegisterModule}

// 模块注册于协商
object DaemonMaster {

  /**
    * 采用持久化流程还是非持久化流程
    *
    * @param name
    * @return
    */
  def moduleProps(name: String, system: ActorSystem, persistent: Boolean = true): Props = {
    name match {
      case `module_flow`  => FlowProxy.props(Array(module_utask, module_auto, module_gtask, module_updater))
      case `module_utask` => UTaskProxy.props(Array(module_flow, module_auto, module_gtask))
      case `module_gtask` => GTaskProxy.props(Array(module_utask))
      case `module_auto`  => AutoMaster.props(Array(module_utask, module_flow))
      case `module_id`    => IdGenerator.props(name, persistent)
      case `module_updater`    => Updater.props(system)
    }
  }

  def props(names: Array[String]) = Props(new DaemonMaster(names))

}

/**
  *
  * @param names
  */
class DaemonMaster(names: Array[String]) extends Actor with ActorLogging {

  import DaemonMaster._

  override def supervisorStrategy: SupervisorStrategy = super.supervisorStrategy

  val idPersistent = context.system.settings.config.getBoolean("flow.id.persistent")

  // start all modules
  var modules = names.map { name =>
    val m = context.actorOf(moduleProps(name, context.system, idPersistent), name)
    context.watch(m)
    FlowRegistry.fillRouteActor(name, m)
    (name, m)
  }.toMap

  override def receive = {
    case GiveMeModule(name) =>
      log.debug(s"收到GiveMeModule(${name}) from [${sender().path}]")
      modules.get(name).foreach(sender() ! RegisterModule(name, _))

    case Terminated(ref) =>
      val (died, rest) = modules.partition(entry => entry._2 == ref);
      modules = rest
      died.foreach { entry =>
        log.warning(s"!!!!!!!!!!!!!!!!!!${entry._1} died, restarting...")
        val m = context.actorOf(moduleProps(entry._1, context.system), entry._1)
        context.watch(m)
        FlowRegistry.fillRouteActor(entry._1, m)
        modules = modules + (entry._1 -> m)
      }
  }
}

