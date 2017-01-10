package com.yimei.zflow.money.utask

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.yimei.zflow.money.utask.Models._

/**
  * Created by hary on 17/1/10.
  */
trait TaskSwear {

  def getSwear: Route = get {
    path("task" / "Swear") {
      complete("get task/Swear")
    }
  }


  def postSwear: Route = post {
    path("task" / "Swear") {
      complete("post task/Swear")
    }
  }
}
