package com.yimei.zflow.cluster

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import com.yimei.zflow.api.GlobalConfig._
import com.yimei.zflow.cluster.flow.FlowClusterSupport
import com.yimei.zflow.cluster.gtask.GroupClusterSupport
import com.yimei.zflow.cluster.utask.UserClusterSupport
import com.yimei.zflow.engine.graph.GraphLoader
import com.yimei.zflow.util.FlywayDB
import com.yimei.zflow.util.id.IdGenerator

/**
  * Created by hary on 16/12/16.
  */
object FlowClusterApp extends FlowClusterSupport
  with GroupClusterSupport
  with UserClusterSupport {

  val common = ConfigFactory.load()

  def main(args: Array[String]): Unit = {
    if (args.isEmpty) throw new IllegalArgumentException("请提供节点编号")
    val nodeId = args(0).toInt
    val nodeConfig = ConfigFactory.load(s"node-${nodeId}.conf").withFallback(common)
    startup(nodeConfig)
  }

  def startup(config: Config): Unit = {
    implicit val system = ActorSystem("FlowSystem", config)
    implicit val materializer = ActorMaterializer()

    val flyway = new FlywayDB(null, null, null)
    flyway.drop()
    flyway.migrate()

    // load flow
    GraphLoader.loadall()

    // start engines and services
    val names = Array(module_auto, module_user, module_flow, module_id, module_group)
    val daemon = system.actorOf(DaemonMaster.props(names), "DaemonMaster")

    // Id服务
    val idGenerator = system.actorOf(IdGenerator.props("id"))

    // 路由
    val all: Route = null;

    // 启动rest服务
    Http().bindAndHandle(all, "0.0.0.0", config.getInt("rest.port"))

  }
}
