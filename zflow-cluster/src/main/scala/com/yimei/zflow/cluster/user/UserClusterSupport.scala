package com.yimei.zflow.cluster.user

import akka.actor.{Actor, ActorInitializationException, ActorSystem, DeathPactException, OneForOneStrategy, Props, SupervisorStrategy}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}
import com.yimei.zflow.api.models.user.Command
import com.yimei.zflow.cluster.FlowClusterApp._
import com.yimei.zflow.engine.user.PersistentUser

/**
  * Created by hary on 16/12/16.
  */
trait UserClusterSupport {
  // for cluster
  val userExtractEntityId: ShardRegion.ExtractEntityId = {
    case cmd: Command => (cmd.guid, cmd)
  }

  val userNumberOfShards = 100

  val userShardName = "user"

  val userExtractShardId: ShardRegion.ExtractShardId = {
    case cmd: Command => (cmd.guid.hashCode % userNumberOfShards).toString
  }
}

object UserProxy {

}

class UserProxy(system: ActorSystem) extends Actor {
  val user = context.actorOf(Props[PersistentUser], "theUser")

  override val supervisorStrategy = OneForOneStrategy() {
    case _: IllegalArgumentException     ⇒ SupervisorStrategy.Resume
    case _: ActorInitializationException ⇒ SupervisorStrategy.Stop
    case _: DeathPactException           ⇒ SupervisorStrategy.Stop
    case _: Exception                    ⇒ SupervisorStrategy.Restart
  }

  def receive = {
    case msg ⇒ user forward msg
  }

 // 用户任务管理
  def region = ClusterSharding(system).start(
      typeName = userShardName,
      entityProps = null,  // 创建用户任务Prop
      settings = ClusterShardingSettings(system),
      extractEntityId = userExtractEntityId,
      extractShardId = userExtractShardId)
}

