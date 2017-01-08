package com.yimei.zflow.engine.auto

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._

/**
  * Created by hary on 16/12/6.
  */

trait AutoRoute extends AutoService {

  /**
    * 手动触发自动恩物
    * POST /auto?flowId=:flowId&taskName=:taskName
    * @return
    */
  def getAuto = path("auto") {
      parameters('flowId, 'taskName) { (flowId, taskName) =>
        complete(s"$taskName + $flowId")
    }
  }

  def autoRoute: Route = getAuto
}


