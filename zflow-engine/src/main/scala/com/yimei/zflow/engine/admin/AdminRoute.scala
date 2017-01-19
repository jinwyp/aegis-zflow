package com.yimei.zflow.engine.admin

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ThymeleafConfig._

/**
  * Created by hary on 17/1/11.
  */
trait AdminRoute extends EditorRoute {

  def devView: Route = get {
    path("dev") {
      pathEndOrSingleSlash {
        th("dev/index", context)
      } ~
        path("task") {
          th("dev/task", context)
        }
    }
  }

  def deployView: Route = get {
    path("deploy") {
      pathEndOrSingleSlash {
        th("deploy/index", context)
      }
    }
  }

  def editorView: Route = get {
    path("editor") {
      pathEndOrSingleSlash {
        th("editor/index", context)
      }
    }
  }

  def monitorView: Route = get {
    path("monitor") {
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
