package com.yimei.zflow.money.utask

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.yimei.zflow.money.utask.Models._

/**
  * Created by hary on 17/1/10.
  */
trait TaskUploadReceipt {

  def getUploadReceipt: Route = get {
    path("task" / "UploadReceipt") {
      complete("get task/UploadReceipt")
    }
  }
  def postUploadReceipt: Route = post {
    path("task" / "UploadReceipt") {
      complete("post task/UploadReceipt")
    }
  }
}


