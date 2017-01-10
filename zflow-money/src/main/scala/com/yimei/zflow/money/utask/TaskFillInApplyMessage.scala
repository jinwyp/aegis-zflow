package com.yimei.zflow.money.utask

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.yimei.zflow.money.utask.Models._

/**
  * Created by hary on 17/1/10.
  */
trait TaskFillInApplyMessage {

  def getFillInApplyMessage: Route = get {
    path("task" / "FillInApplyMessage") {
      complete("get task/FillInApplyMessage")
    }
  }
  def postFillInApplyMessage: Route = post {
    path("task" / "FillInApplyMessage") {
      complete("post task/FillInApplyMessage")
    }
  }
}


