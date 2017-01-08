package com.yimei.zflow.cluster.group

import akka.actor.{Actor, ActorInitializationException, ActorSystem, DeathPactException, OneForOneStrategy, Props, SupervisorStrategy}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}
import com.yimei.zflow.api.models.group.Command
import com.yimei.zflow.cluster.FlowClusterApp._
import com.yimei.zflow.engine.group.PersistentGroup

/**
  * Created by hary on 16/12/16.
  */
trait GroupClusterSupport {
  // for cluster
  val groupExtractEntityId: ShardRegion.ExtractEntityId = {
    case cmd: Command => (cmd.ggid, cmd)
  }

  val groupNumberOfShards = 100

  val groupExtractShardId: ShardRegion.ExtractShardId = {
    case cmd: Command => (cmd.ggid.hashCode % groupNumberOfShards).toString
  }

  val groupShardName = "group"
}

object GroupProxy {

}

class GroupProxy(system: ActorSystem) extends Actor {
  val group = context.actorOf(Props[PersistentGroup], "theGroup")

  override val supervisorStrategy = OneForOneStrategy() {
    case _: IllegalArgumentException ⇒ SupervisorStrategy.Resume
    case _: ActorInitializationException ⇒ SupervisorStrategy.Stop
    case _: DeathPactException ⇒ SupervisorStrategy.Stop
    case _: Exception ⇒ SupervisorStrategy.Restart
  }

  def receive = {
    case msg ⇒ group forward msg
  }

  // 任务组管理
  def region() = ClusterSharding(system).start(
      typeName = groupShardName,
      entityProps = null,     // 创建任务组Prop
      settings = ClusterShardingSettings(system),
      extractEntityId = groupExtractEntityId,
      extractShardId = groupExtractShardId)
}

