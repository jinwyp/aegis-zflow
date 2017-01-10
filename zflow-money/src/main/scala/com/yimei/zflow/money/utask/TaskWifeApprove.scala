package com.yimei.zflow.money.utask

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.yimei.zflow.money.utask.Models._

/**
  * Created by hary on 17/1/10.
  */
trait TaskWifeApprove {
  def getWifeApprove: Route = get {
    path("task" / "WifeApprove") {
      complete("get task/WifeApprove")
    }
  }
  def postWifeApprove: Route = post {
    path("task" / "WifeApprove") {
      complete("post task/WifeApprove")
    }
  }
}
