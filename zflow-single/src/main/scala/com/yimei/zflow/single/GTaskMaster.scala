package com.yimei.zflow.single

import akka.actor.{ActorRef, Props, Terminated}
import com.yimei.zflow.api.GlobalConfig._
import com.yimei.zflow.api.models.group.{Command, CommandCreateGroup, CommandQueryGroup}
import com.yimei.zflow.engine.gtask.{MemoryGTask, PersistentGTask}
import com.yimei.zflow.util.module.{ModuleMaster, ServicableBehavior}

object GTaskMaster {
  def props(dependOn: Array[String]): Props = Props(new GTaskMaster(dependOn))
}

/**
  * Created by hary on 16/12/12.
  */
class GTaskMaster(dependOn: Array[String])
  extends ModuleMaster(module_gtask, dependOn)
  with ServicableBehavior {

  def create(ggid: String): ActorRef = {

    val prop = context.system.settings.config.getBoolean("flow.group.persistent") match {
      case true  =>  {
        log.info(s"创建persistent group")
        Props(new PersistentGTask(modules, 20))
      }
      case false => {
        log.info(s"创建non-persistent group")
        Props(new MemoryGTask(modules))
      }
    }
    context.actorOf(prop,ggid)
  }

  override def serving: Receive = {
    case cmd@CommandCreateGroup(ggid) =>
      log.info(s"GroupMaster 收到消息${cmd}")
      val child = context.child(ggid).fold(create(ggid))(identity)
      child forward CommandQueryGroup(ggid)

    case command: Command =>
      val child = context.child(command.ggid).fold(create(command.ggid))(identity)
      child forward command

    case Terminated(child) =>
      log.info(s"${child.path.name} terminated")
  }

}
