package com.yimei.zflow.cluster

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.LogEntry
import akka.http.scaladsl.server.{Route, RouteResult}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import com.yimei.zflow.api.GlobalConfig._
import com.yimei.zflow.engine.EngineRoute
import com.yimei.zflow.engine.graph.GraphLoader
import com.yimei.zflow.util.FlywayDB
import com.yimei.zflow.util.organ.OrganRoute

import scala.concurrent.duration._


/**
  * Created by hary on 16/12/16.
  */
object FlowClusterApp {

  def main(args: Array[String]): Unit = {
    if (args.isEmpty) throw new IllegalArgumentException("请提供节点编号")
    val nodeId = args(0).toInt
    startup(nodeId)
  }

  def startup(nodeId: Int): Unit = {

    val config = ConfigFactory.load(s"node-${nodeId}")

    implicit val system = ActorSystem("FlowCluster", config)
    implicit val materializer = ActorMaterializer()

    val flyway = new FlywayDB(config.getString("database.jdbcUrl"), config.getString("database.username"), config.getString("database.password"))
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
      override val log: LoggingAdapter = Logging(coreSystem, getClass)

      override val utaskTimeout: Timeout = Timeout(3 seconds)
      override val flowServiceTimeout: Timeout = Timeout(3 seconds)
      override val gtaskTimeout: Timeout = Timeout(3 seconds)

      def route = engineRoute ~ organRoute
    }

    def extractLogEntry(req: HttpRequest): RouteResult => Option[LogEntry] = {
      case RouteResult.Complete(res) => Some(LogEntry(req.method.name + ": " + res.status, Logging.InfoLevel))
      case _ => None // no log entries for rejections
    }

    val all: Route = logRequestResult(extractLogEntry _) {
      pathPrefix("api") {
        AppRoute.route
      }
    }


    // 启动rest服务
    Http().bindAndHandle(all, "0.0.0.0", config.getInt("rest.port"))

  }
}
