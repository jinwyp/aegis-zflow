package com.yimei.zflow.engine.admin

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ThymeleafConfig._

/**
  * Created by hary on 17/1/11.
  */
trait AdminRoute {

  def deployView: Route = get {
    path( "deploy") {
      th("deploy", context)
    }
  }

  def editorView: Route = get {
    path("editor") {
      th("editor", context)
    }
  }

  def graphView: Route = get {
    path("graph") {
      th("graph", context)
    }
  }

  def indexView: Route = get {
      pathEndOrSingleSlash {
        th("index", context)
      }
  }

  // 测试用, 生产可能在nginx上
  def static: Route = pathPrefix("static") {
    getFromDirectory("zflow-admin/frontend")
  }

  def adminRoute: Route = deployView ~ editorView ~ graphView ~ indexView ~ static
}
