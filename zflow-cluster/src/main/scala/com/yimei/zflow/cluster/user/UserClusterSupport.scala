package com.yimei.zflow.cluster.user

import akka.actor.{Actor, ActorInitializationException, ActorRef, ActorSystem, DeathPactException, OneForOneStrategy, Props, SupervisorStrategy}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}
import com.yimei.zflow.api.GlobalConfig._
import com.yimei.zflow.api.models.user.Command
import com.yimei.zflow.engine.group.PersistentGroup
import com.yimei.zflow.engine.user.PersistentUser
import com.yimei.zflow.util.module.ModuleMaster

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
  def props(dependOn: Array[String]): Props = Props(new UserProxy(dependOn))
}

/**
  * Flow依赖于user, group, id, auto
  */
class UserProxy(dependOn: Array[String]) extends ModuleMaster(module_flow, dependOn)
  with Actor
  with UserClusterSupport {

  var region: ActorRef = null

  override def initHook() = {
    ClusterSharding(context.system).start(
      typeName = userShardName,
      entityProps = Props(new PersistentUser(modules, 3)),
      settings = ClusterShardingSettings(context.system),
      extractEntityId = userExtractEntityId,
      extractShardId = userExtractShardId)
  }

  def serving: Receive = {
    case command: Command =>
      region forward command
  }
}

