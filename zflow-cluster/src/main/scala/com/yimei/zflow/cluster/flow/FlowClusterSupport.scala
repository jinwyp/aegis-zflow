package com.yimei.zflow.cluster.flow

import akka.actor.{Actor, ActorIdentity, ActorRef, Identify, Props, ReceiveTimeout}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}
import com.yimei.zflow.api.GlobalConfig._
import com.yimei.zflow.api.models.flow.Command
import com.yimei.zflow.engine.flow.PersistentFlow
import com.yimei.zflow.util.module.ModuleMaster

import scala.concurrent.duration._

/**
  * Created by hary on 16/12/16.
  */
trait FlowClusterSupport {
  // for cluster
  val flowExtractEntityId: ShardRegion.ExtractEntityId = {
    case cmd: Command => (cmd.flowId, cmd)
  }

  val flowNumberOfShards = 100

  val flowExtractShardId: ShardRegion.ExtractShardId = {
    case cmd: Command => (cmd.flowId.hashCode % flowNumberOfShards).toString
  }

  val flowShardName = "flow"
}

object FlowProxy {
  def props(dependOn: Array[String]): Props = Props(new FlowProxy(dependOn))
}

/**
  * Flow依赖于user, group, id, auto
  */
class FlowProxy(dependOn: Array[String]) extends ModuleMaster(module_flow, dependOn)
  with Actor
  with FlowClusterSupport {

  var region: ActorRef = null
  override def initHook() =  {
    ClusterSharding(context.system).start(
      typeName = flowShardName,
      entityProps = Props(new PersistentFlow(modules)),
      settings = ClusterShardingSettings(context.system),
      extractEntityId = flowExtractEntityId,
      extractShardId = flowExtractShardId)
  }

  def serving: Receive = {
    case command: Command =>
      region forward command
  }
}

