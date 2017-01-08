package com.yimei.zflow.cluster

import akka.actor.ActorSystem
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import com.yimei.zflow.cluster.flow.FlowClusterSupport
import com.yimei.zflow.cluster.group.GroupClusterSupport
import com.yimei.zflow.cluster.user.UserClusterSupport
import com.yimei.zflow.util.id.IdGenerator

/**
  * Created by hary on 16/12/16.
  */
object FlowClusterApp extends FlowClusterSupport
  with GroupClusterSupport
  with UserClusterSupport {

  val common = ConfigFactory.load()

  override val groupNumberOfShards: Int = common.getInt("zflow.shard.group")
  override val userNumberOfShards: Int  = common.getInt("zflow.shard.user")
  override val flowNumberOfShards: Int  = common.getInt("zflow.shard.flow")

  def main(args: Array[String]): Unit = {
    if (args.isEmpty) throw new IllegalArgumentException("请提供节点编号")
    val nodeId = args(0).toInt
    val nodeConfig = ConfigFactory.load(s"node-${nodeId}.conf").withFallback(common)
    startup(nodeConfig)
  }

  def startup(config: Config): Unit = {
    implicit val system = ActorSystem("FlowSystem", config)
    implicit val materializer = ActorMaterializer()

    // Id服务
    val idGenerator = system.actorOf(IdGenerator.props("id"))

    // 自动任务
    // val auto = system.actorOf(AutoMaster.props(Array("abc")))

    // 路由
    val all: Route = null;

    // 启动rest服务
    Http().bindAndHandle(all, "0.0.0.0", config.getInt("rest.port"))

  }
}
