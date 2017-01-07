package com.yimei.zflow.single

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import com.yimei.zflow.engine.graph.GraphLoader
import com.yimei.zflow.api.GlobalConfig._
import com.yimei.zflow.engine.{EngineRoute, FlowRegistry}
import com.yimei.zflow.util.{FlowExceptionHandler, FlywayDB}

/**
  * Created by hary on 17/1/6.
  */

object FlowApp extends App with FlowExceptionHandler with EngineRoute {

  implicit val coreSystem = ActorSystem("FlowSystem")
  val config = coreSystem.settings.config

  // 数据库迁移
  val jdbcUrl = config.getString("database.url")
  val username = config.getString("database.user")
  val password = config.getString("database.password")
  val flyway = new FlywayDB(jdbcUrl, username, password)
  flyway.drop()
  flyway.migrate()

  // 加载流程
  GraphLoader.loadall()

  // 启动流程
  val names = Array(module_auto, module_user, module_flow, module_id, module_group)
  val daemon = coreSystem.actorOf(DaemonMaster.props(names)(jdbcUrl, username, password), "DaemonMaster")

  Thread.sleep(2000);

  //  var root: Route = pathPrefix("cang") {
  //    CangFlowRoute.route() ~
  //      CangUserRoute.route() ~
  //      SessionDemoRoute.route()
  //  }

  // 3> http
  val base: Route = pathPrefix("api") {
    engineRoute
  }
  def |+|(left: Route, right: Route) = left ~ right
  val empty: Route = get {
    path("impossible") {
      complete("impossible")
    }
  }
  val flowRoute = FlowRegistry.registries.map { entry =>
    pathPrefix(entry._1) {
      entry._2.routes.map(_ (null)).foldLeft(empty)(|+|)   // todo
    }
  }.foldLeft(empty)(|+|)
  val all = logRequest("debug") {
    base ~ flowRoute
  }


  println(s"http is listening on ${config.getInt("http.port")}")
  Http().bindAndHandle(all, "0.0.0.0", config.getInt("http.port"))
}


