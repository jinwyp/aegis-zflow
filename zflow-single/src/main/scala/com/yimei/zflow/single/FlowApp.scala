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
import com.yimei.zflow.engine.admin.CodeEngine.{CodeConfig, CodeMeta, CodePoints}
import com.yimei.zflow.engine.admin.{AdminRoute, CodeEngine}
import com.yimei.zflow.engine.graph.GraphLoader
import com.yimei.zflow.engine.{EngineRoute, FlowRegistry, GenModule}
import com.yimei.zflow.util.config.Core
import com.yimei.zflow.util.organ.OrganRoute
import com.yimei.zflow.util.{FlowExceptionHandler, FlywayDB}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class GenModule(system: ActorSystem) extends { override implicit val coreSystem: ActorSystem = system}  with Core {

  implicit val httpExecutionContext = coreSystem.dispatcher

  def gen() = {
    val meta = CodeMeta("com.yimei.zflow", "money", "Money", "MoneyApp.scala")
    import java.util.{HashMap => JMap}
    val code = new JMap[String, AnyRef]() {
      put("meta", meta)
    }
    CodeEngine.genFile("XFlowApp.ftl", code, "/tmp", "aegis-zflow-money")
      .foreach { case (filename, result) =>
        println(s"${filename} => ${result.status}, ${result.count} bytes read.")
      }

    val codeModel = new JMap[String, AnyRef] {
      put("meta", meta.copy(filename = "Models.scala"))
    }
    CodeEngine.genFile("utask.XFlowModels.ftl", codeModel, "/tmp", "aegis-zflow-money")
      .foreach { case (filename, result) =>
        println(s"${filename} => ${result.status}, ${result.count} bytes read.")
      }

    val codeConfig = new JMap[String, AnyRef] {
      put("meta", meta.copy(filename = "MoneyConfig.scala"))
      put("code", CodeConfig(
        new JMap[String, String]{ put("a", "a"); put("b", "b")},
        new JMap[String, String]{ put("V", "V"); put("W", "W")},
        new JMap[String, Edge]{ put("E1", Edge(name = "E1", end = "end")) },
        new JMap[String, TaskInfo]{ put("U1", TaskInfo("U1", List("a", "b", "c"))) },
        new JMap[String, TaskInfo]{ put("A1", TaskInfo("A1", List("X", "Y", "Z"))) }
      ))
    }
    CodeEngine.genFile("config.XFlowConfig.ftl", codeConfig, "/tmp", "aegis-zflow-money")
      .foreach { case (filename, result) =>
        println(s"${filename} => ${result.status}, ${result.count} bytes read.")
      }

    val codeGraph = new JMap[String, AnyRef] {
      put("meta", meta.copy(filename = "MoneyGraph.scala"))
      put("code", CodeConfig(
        new JMap[String, String]{ put("a", "a"); put("b", "b")},
        new JMap[String, String]{ put("V", "V"); put("W", "W")},
        new JMap[String, Edge]{ put("E1", Edge(name = "E1", end = "end")) },
        new JMap[String, TaskInfo]{ put("U1", TaskInfo("U1", List("a", "b", "c"))) },
        new JMap[String, TaskInfo]{ put("A1", TaskInfo("A1", List("X", "Y", "Z"))) }
      ))
    }
    CodeEngine.genFile("XFlowGraph.ftl", codeGraph, "/tmp", "aegis-zflow-money")
      .foreach { case (filename, result) =>
        println(s"${filename} => ${result.status}, ${result.count} bytes read.")
      }


    // 生成 config/MoneyPoints.scala

    val codePoints = new JMap[String, AnyRef] {
      put("meta", meta.copy(filename = "MoneyPoints.scala"))
      put("code", CodePoints( new JMap[String, String]{ put("a", "a"); put("b", "b")}))
    }
    CodeEngine.genFile("config.XFlowPoints.ftl", codePoints, "/tmp", "aegis-zflow-money")
      .foreach { case (filename, result) =>
        println(s"${filename} => ${result.status}, ${result.count} bytes read.")
      }

    // 生成 UTaskRoute
    val codeUTaskRoute = new JMap[String, AnyRef] {
      put("meta", meta.copy(filename = "UTaskRoute.scala"))
      put("code", Array("T1", "T2"))
    }
    CodeEngine.genFile("utask.UTaskRoute.ftl", codeUTaskRoute, "/tmp", "aegis-zflow-money")
      .foreach { case (filename, result) =>
        println(s"${filename} => ${result.status}, ${result.count} bytes read.")
      }

  }


}

/**
  * Created by hary on 17/1/6.
  */
object FlowApp extends {
  implicit val coreSystem = ActorSystem("FlowSystem")
} with App with FlowExceptionHandler with EngineRoute with OrganRoute with AdminRoute {

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

  new GenModule(coreSystem).gen()

  // prepare routes
  val route: Route =
    logRequestResult(extractLogEntry _) {
      pathPrefix("zflow" / "api") { engineRoute } ~
      pathPrefix("organ" / "api") { organRoute } ~
      pathPrefix("zflow") { adminRoute } ~
      FlowRegistry.routes
    }

  // start http server
  implicit val httpExecutionContext = coreSystem.dispatcher
  println(s"http is listening on ${config.getInt("http.port")}")
  Http().bindAndHandle(route, "0.0.0.0", config.getInt("http.port"))
}

