package com.yimei.zflow.cluster

import akka.actor.{Actor, ActorLogging, Props, Terminated}
import com.yimei.zflow.api.GlobalConfig._
import com.yimei.zflow.api.models.flow.{Command, CommandCreateFlow}
import com.yimei.zflow.api.models.user.{Command => UserCommand}
import com.yimei.zflow.cluster.flow.FlowProxy
import com.yimei.zflow.cluster.gtask.{GTaskProxy}
import com.yimei.zflow.cluster.utask.{UTaskProxy}
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
  def moduleProps(name: String, persistent: Boolean = true): Props = {
    name match {
      case `module_flow`  => FlowProxy.props(Array(module_user, module_auto, module_group, module_id))
      case `module_user`  => UTaskProxy.props(Array(module_flow, module_auto, module_group, module_id))
      case `module_group` => GTaskProxy.props(Array(module_user))
      case `module_auto`  => AutoMaster.props(Array(module_user, module_flow, module_id))
      case `module_id`    => IdGenerator.props(name, 0, persistent)
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

  val idPersistent = context.system.settings.config.getBoolean("flow.id.persistent")

  var modules = names.map { name =>
    val m = context.actorOf(moduleProps(name, idPersistent))
    context.watch(m)
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
        val m = context.actorOf(moduleProps(entry._1), entry._1)
        context.watch(m)
        modules = modules + (entry._1 -> m)
      }

    ////////////////////////////////////////////////////////////////////////
    // 测试Flow
    ////////////////////////////////////////////////////////////////////////
    case cmd: CommandCreateFlow =>
      log.debug(s"收到${cmd}")
      modules.get(module_flow).foreach(_ forward cmd)

    case cmd: Command =>
      log.debug(s"收到${cmd}")
      modules.get(module_flow).foreach(_ forward cmd)

    ////////////////////////////////////////////////////////////////////////
    // 测试user
    ////////////////////////////////////////////////////////////////////////
    case cmd: UserCommand =>
      log.debug(s"收到$cmd")
      modules.get(module_user).foreach(_ forward cmd)
  }
}

