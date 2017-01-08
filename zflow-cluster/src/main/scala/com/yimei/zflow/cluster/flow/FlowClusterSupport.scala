package com.yimei.zflow.cluster.flow

import akka.actor.{Actor, ActorSystem}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}
import com.yimei.zflow.api.models.flow.Command
import com.yimei.zflow.cluster.FlowClusterApp._

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

}

class FlowProxy(system: ActorSystem) extends Actor {
  override def receive: Receive = identify

  def identify: Receive = {
    case _ => "ok"
  }


  // 流程管理 - 需要idGenerator
  def region = ClusterSharding(system).start(
    typeName = flowShardName,
    entityProps = null, //  创建流程的Prop
    settings = ClusterShardingSettings(system),
    extractEntityId = flowExtractEntityId,
    extractShardId = flowExtractShardId)

}

