package com.yimei.zflow.single

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import com.yimei.zflow.api.GlobalConfig._
import com.yimei.zflow.engine.EngineRoute
import com.yimei.zflow.engine.graph.GraphLoader
import com.yimei.zflow.util.organ.OrganRoute
import com.yimei.zflow.util.{FlowExceptionHandler, FlywayDB}

/**
  * Created by hary on 17/1/6.
  */

object FlowApp extends {
  implicit val coreSystem = ActorSystem("FlowSystem")
} with App with FlowExceptionHandler with EngineRoute with OrganRoute {

  val jdbcUrl = coreConfig.getString("database.jdbcUrl")
  val username = coreConfig.getString("database.username")
  val password = coreConfig.getString("database.password")


  val flyway = new FlywayDB(jdbcUrl, username, password)
  flyway.drop()
  flyway.migrate()

  // load flow
  GraphLoader.loadall()

  // start engines and services
  val names = Array(module_auto, module_user, module_flow, module_id, module_group)
  val daemon = coreSystem.actorOf(DaemonMaster.props(names)(jdbcUrl, username, password), "DaemonMaster")

  // prepare routes
  val route: Route = pathPrefix("api") {
    engineRoute ~ organRoute
  }

  // start http server
  println(s"http is listening on ${coreConfig.getInt("http.port")}")
  Http().bindAndHandle(route, "0.0.0.0", coreConfig.getInt("http.port"))
}


