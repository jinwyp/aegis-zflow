package com.yimei.zflow.engine.admin

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ThymeleafConfig._

/**
  * Created by hary on 17/1/11.
  */
trait AdminRoute {

  def devView: Route = get {
    path("dev") {
      th("dev", context)
    }
  }

  def taskView: Route = get {
    path("task") {
      th("task", context)
    }
  }

  def deployView: Route = get {
    path("deploy") {
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

  def adminRoute: Route =
    pathPrefix("admin") {
      devView ~ taskView ~ deployView ~ editorView ~ graphView ~ indexView
    } ~ pathPrefix("static") {
      getFromDirectory("../zflow-admin/static")
    }

}
