package com.yimei.zflow.engine.auto

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._

/**
  * Created by hary on 16/12/6.
  */

trait AutoRoute extends AutoService {

  def getData = path("auto") {
      parameters('flowId, 'taskName) { (flowId, taskName) =>
        complete(s"$taskName + $flowId")
    }
  }

  def route: Route = getData
}



