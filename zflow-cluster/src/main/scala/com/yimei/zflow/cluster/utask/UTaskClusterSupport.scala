package com.yimei.zflow.cluster.utask

import akka.actor.{Actor, ActorRef, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}
import com.yimei.zflow.api.GlobalConfig._
import com.yimei.zflow.api.models.user.Command
import com.yimei.zflow.engine.utask.PersistentUTask
import com.yimei.zflow.util.module.ModuleMaster

/**
  * Created by hary on 16/12/16.
  */
trait UTaskClusterSupport {
  // for cluster
  val utaskExtractEntityId: ShardRegion.ExtractEntityId = {
    case cmd: Command => (cmd.guid, cmd)
  }

  val utaskNumberOfShards = 100

  val utaskShardName = "utask"

  val utaskExtractShardId: ShardRegion.ExtractShardId = {
    case cmd: Command => (cmd.guid.hashCode % utaskNumberOfShards).toString
  }
}

object UTaskProxy {
  def props(dependOn: Array[String]): Props = Props(new UTaskProxy(dependOn))
}

/**
  * Flow依赖于user, group, id, auto
  */
class UTaskProxy(dependOn: Array[String]) extends ModuleMaster(module_flow, dependOn)
  with Actor
  with UTaskClusterSupport {

  var region: ActorRef = null

  override def initHook() = {
    ClusterSharding(context.system).start(
      typeName = utaskShardName,
      entityProps = Props(new PersistentUTask(modules, 3)),
      settings = ClusterShardingSettings(context.system),
      extractEntityId = utaskExtractEntityId,
      extractShardId = utaskExtractShardId)
  }

  def serving: Receive = {
    case command: Command =>
      region forward command
  }
}

