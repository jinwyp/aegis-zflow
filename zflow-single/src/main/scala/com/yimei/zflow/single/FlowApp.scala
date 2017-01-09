package com.yimei.zflow.single

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.util.Timeout
import scala.concurrent.duration._
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

  override val utaskTimeout: Timeout = Timeout(3 seconds)
  override val flowServiceTimeout: Timeout =  Timeout(3 seconds)
  override val gtaskTimeout: Timeout =  Timeout(3 seconds)

  val config = coreSystem.settings.config
  val jdbcUrl = config.getString("database.jdbcUrl")
  val username = config.getString("database.username")
  val password = config.getString("database.password")

  val flyway = new FlywayDB(jdbcUrl, username, password)
  flyway.drop()
  flyway.migrate()

  // load flow
  GraphLoader.loadall()

  // start engines and services
  val names = Array(module_auto, module_utask, module_flow, module_id, module_gtask)
  val daemon = coreSystem.actorOf(DaemonMaster.props(names), "DaemonMaster")

  // prepare routes
  val route: Route =
    logRequest("debug") {
      pathPrefix("api") {
        engineRoute ~ organRoute
      }
    }

  implicit val httpExecutionContext = coreSystem.dispatcher

  // start http server
  println(s"http is listening on ${config.getInt("http.port")}")
  Http().bindAndHandle(route, "0.0.0.0", config.getInt("http.port"))
}


