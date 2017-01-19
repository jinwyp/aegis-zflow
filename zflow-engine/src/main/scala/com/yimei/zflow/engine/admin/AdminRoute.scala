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
      th("dev", context)
    } ~
    path("task") {
      th("task", context)
    }
  }

  def deployView: Route = get {
    path("deploymain") {
      th("deploy", context)
    }
  }

  def editorView: Route = get {
    path("editor") {
      th("floweditor/editor", context)
    }
  }

  def graphView: Route = get {
    path("graph") {
      th("graph", context)
    }
  }

  def monView: Route = get {
    path("flowmon") {
      pathEndOrSingleSlash {
        th("index", context)
      } ~ path("graph") {
        th("flowmon/graph", context)
      }
    }
  }


  val prefix = "../zflow-admin/frontend/"

  def adminRoute: Route =
    pathPrefix("admin") {
      devView ~ deployView ~ editorView ~ monView ~ editorRoute
    } ~ pathPrefix("static") {
      getFromDirectory(prefix)
    }

}
