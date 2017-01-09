package com.yimei.zflow.single

import akka.actor.{Props, Terminated}
import com.yimei.zflow.api.GlobalConfig._
import com.yimei.zflow.api.models.user.{CommandCreateUser, CommandQueryUser, CommandUserTask, Command => UserCommand}
import com.yimei.zflow.engine.user.{MemoryUser, PersistentUser}
import com.yimei.zflow.util.module.{ModuleMaster, ServicableBehavior}

object UserMaster {


  def props(dependOn: Array[String], persist: Boolean = true) = Props(new UserMaster(dependOn))

}

/**
  * Created by hary on 16/12/2.
  */
class UserMaster(dependOn: Array[String])
  extends ModuleMaster(module_user, dependOn)
  with ServicableBehavior {

  override def serving: Receive = {

    case cmd@CommandCreateUser(guid) =>
      log.debug(s"UserMaster 收到消息${cmd}")
      val child = context.child(guid).fold(create(guid))(identity)
      child forward CommandQueryUser(guid)

    // 收到流程过来的任务
    case command: CommandUserTask =>
      val child = context.child(command.guid).fold {
        create(command.guid)
      }(identity)
      child forward command

    // 其他用户command
    case command: UserCommand =>
      val child = context.child(command.guid).fold {
        create(command.guid)
      }(identity)
      child forward command

    case Terminated(child) =>
      log.info(s"${child.path.name} terminated")
  }

  private def create(guid: String) = {

    val prop = context.system.settings.config.getBoolean("flow.user.persistent") match {
      case true  =>  {
        log.debug(s"创建persistent user")
        Props(new PersistentUser(modules, 20))
      }
      case false => {
        log.debug(s"创建non-persistent user")
        Props(new MemoryUser(modules))
      }
    }
    context.actorOf(prop, guid)
  }

}



