package com.yimei.zflow.money.tasks

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import Models._

/**
  * Created by hary on 17/1/9.
  */
trait TaskApprove extends SprayJsonSupport {

  def getDivination: Route = get {
    path("approve") {
      complete(ApproveView("hello"))
    }
  }

  def postDivination: Route = post {
    path("approve") {
      complete(ApproveSubmit(1, 2))
    }
  }

  def approveRoute = getDivination ~ postDivination
}
