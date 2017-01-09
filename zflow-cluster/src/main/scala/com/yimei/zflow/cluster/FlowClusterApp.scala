package com.yimei.zflow.cluster

import akka.actor.{ActorRef, ActorSystem}
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import scala.concurrent.duration._
import com.typesafe.config.{Config, ConfigFactory}
import com.yimei.zflow.api.GlobalConfig._
import com.yimei.zflow.engine.EngineRoute
import com.yimei.zflow.engine.graph.GraphLoader
import com.yimei.zflow.util.FlywayDB
import com.yimei.zflow.util.id.IdGenerator
import com.yimei.zflow.util.organ.OrganRoute


/**
  * Created by hary on 16/12/16.
  */
object FlowClusterApp {

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
    val names = Array(module_auto, module_utask, module_flow, module_id, module_gtask)
    val daemon = system.actorOf(DaemonMaster.props(names), "DaemonMaster")

    // 路由
    object AppRoute extends {
      implicit val coreSystem = system
    } with OrganRoute with EngineRoute {
      override def log: LoggingAdapter = Logging(coreSystem, getClass)
      override val utaskTimeout: Timeout = Timeout(3 seconds)
      override val flowServiceTimeout: Timeout = Timeout(3 seconds)
      override val gtaskTimeout: Timeout = Timeout(3 seconds)

      def route = engineRoute ~ organRoute
    }

    val all: Route = logRequest("debug") {
      pathPrefix("api") {
        AppRoute.route
      }
    }

    // 启动rest服务
    Http().bindAndHandle(all, "0.0.0.0", config.getInt("rest.port"))

  }
}
