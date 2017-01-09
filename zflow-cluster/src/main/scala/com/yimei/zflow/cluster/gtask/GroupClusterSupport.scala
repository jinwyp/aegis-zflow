package com.yimei.zflow.cluster.gtask

import akka.actor.{Actor, ActorRef, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}
import com.yimei.zflow.api.GlobalConfig._
import com.yimei.zflow.api.models.group.Command
import com.yimei.zflow.engine.gtask.PersistentGTask
import com.yimei.zflow.util.module.ModuleMaster

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
  def props(dependOn: Array[String]): Props = Props(new GroupProxy(dependOn))
}

/**
  * Flow依赖于user, group, id, auto
  */
class GroupProxy(dependOn: Array[String]) extends ModuleMaster(module_flow, dependOn)
  with Actor
  with GroupClusterSupport {

  var region: ActorRef = null

  override def initHook() = {
    ClusterSharding(context.system).start(
      typeName = groupShardName,
      entityProps = Props(new PersistentGTask(modules, 3)),
      settings = ClusterShardingSettings(context.system),
      extractEntityId = groupExtractEntityId,
      extractShardId = groupExtractShardId)
  }

  def serving: Receive = {
    case command: Command =>
      region forward command
  }
}

