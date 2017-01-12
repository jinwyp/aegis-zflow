package com.yimei.zflow.single

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.LogEntry
import akka.util.Timeout
import com.yimei.zflow.api.GlobalConfig._
import com.yimei.zflow.api.models.flow.{Edge, TaskInfo}
import com.yimei.zflow.api.models.graph.{GraphConfig, GraphConfigProtocol}
import com.yimei.zflow.engine.admin.CodeEngine.{CodeConfig, CodeMeta, CodePoints}
import com.yimei.zflow.engine.admin.{AdminRoute, CodeEngine}
import com.yimei.zflow.engine.graph.GraphLoader
import com.yimei.zflow.engine.{EngineRoute, FlowRegistry}
import com.yimei.zflow.util.config.Core
import com.yimei.zflow.util.organ.OrganRoute
import com.yimei.zflow.util.{FlowExceptionHandler, FlywayDB}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.io.Source

/**
  * Created by hary on 17/1/6.
  */
object FlowApp extends {
  implicit val coreSystem = ActorSystem("FlowSystem")
} with App with FlowExceptionHandler with EngineRoute with OrganRoute with AdminRoute with GraphConfigProtocol{

  override val utaskTimeout = Timeout(3.seconds)
  override val flowServiceTimeout = Timeout(3.seconds)
  override val gtaskTimeout = Timeout(3.seconds)

  val config = coreSystem.settings.config
  val jdbcUrl = config.getString("database.jdbcUrl")
  val username = config.getString("database.username")
  val password = config.getString("database.password")

  val flyway = new FlywayDB(jdbcUrl, username, password)
  flyway.drop()
  flyway.migrate()

  // load flow
  GraphLoader.loadall()
  // FlowRegistry.register("cang", CangGraph)

  // start engines and services
  val names = Array(module_auto, module_utask, module_flow, module_gtask, module_id, module_updater)
  val daemon = coreSystem.actorOf(DaemonMaster.props(names), "DaemonMaster")

  def extractLogEntry(req: HttpRequest): RouteResult => Option[LogEntry] = {
    case RouteResult.Complete(res) => Some(LogEntry(req.method.name + " " + req.uri + " => " + res.status, Logging.InfoLevel))
    case _ => None // no log entries for rejections
  }



  // prepare routes
  val route: Route =
    logRequestResult(extractLogEntry _) {
      pathPrefix("zflow" / "api") {
        engineRoute
      } ~
        pathPrefix("organ" / "api") {
          organRoute
        } ~
        pathPrefix("zflow") {
          adminRoute
        } ~
        FlowRegistry.routes
    }

  // start http server
  implicit val httpExecutionContext = coreSystem.dispatcher
  println(s"http is listening on ${config.getInt("http.port")}")
  Http().bindAndHandle(route, "0.0.0.0", config.getInt("http.port"))
}

