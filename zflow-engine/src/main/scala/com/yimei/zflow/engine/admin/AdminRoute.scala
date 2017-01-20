package com.yimei.zflow.engine.admin

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ThymeleafConfig._

/**
  * Created by hary on 17/1/11.
  */
trait AdminRoute {

  // 开发
  def devView: Route = get {
    pathPrefix("dev") {
      pathEndOrSingleSlash {
        th("dev/index", context)
      } ~
        path("task") {
          th("dev/task", context)
        }
    }
  }

  // 部署
  def deployView: Route = get {
    pathPrefix("deploy") {
      pathEndOrSingleSlash {
        th("deploy/index", context)
      }
    }
  }

  // 设计流程
  def editorView: Route = get {
    pathPrefix("editor") {
      pathEndOrSingleSlash {
        th("editor/index", context)
      }
    }
  }

  // 监控
  def monitorView: Route = get {
    pathPrefix("monitor") {
      pathEndOrSingleSlash {
        th("monitor/index", context)
      } ~
      path("graph") {
        th("monitor/graph", context)
      }
    }
  }

  val prefix = "../zflow-admin/frontend/"

  def adminRoute: Route =
    pathPrefix("admin") {
      devView ~ deployView ~ editorView ~ monitorView
    } ~ pathPrefix("static") {
      getFromDirectory(prefix)
    }

}
