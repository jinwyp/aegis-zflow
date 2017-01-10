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
trait GTaskClusterSupport {
  // for cluster
  val gtaskExtractEntityId: ShardRegion.ExtractEntityId = {
    case cmd: Command => (cmd.ggid, cmd)
  }

  val gtaskNumberOfShards = 100

  val gtaskExtractShardId: ShardRegion.ExtractShardId = {
    case cmd: Command => (cmd.ggid.hashCode % gtaskNumberOfShards).toString
  }

  val gtaskShardName = "group"
}

object GTaskProxy {
  def props(dependOn: Array[String]): Props = Props(new GTaskProxy(dependOn))
}

/**
  * Flow依赖于user, group, id, auto
  */
class GTaskProxy(dependOn: Array[String]) extends ModuleMaster(module_gtask, dependOn)
  with Actor
  with GTaskClusterSupport {

  var region: ActorRef = null

  override def initHook() = {
    region = ClusterSharding(context.system).start(
      typeName = gtaskShardName,
      entityProps = Props(new PersistentGTask(modules, 3)),
      settings = ClusterShardingSettings(context.system),
      extractEntityId = gtaskExtractEntityId,
      extractShardId = gtaskExtractShardId)
    log.info("GTask Region started")
  }

  def serving: Receive = {
    case command: Command =>
      region forward command
  }
}

