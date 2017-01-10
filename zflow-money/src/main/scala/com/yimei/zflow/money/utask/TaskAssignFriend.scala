package com.yimei.zflow.money.utask

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.yimei.zflow.money.utask.Models._

/**
  * Created by hary on 17/1/10.
  */
trait TaskAssignFriend {

  def getAssignFriend: Route = get {
    path("task" / "AssignFriend" ) {
      complete("get task/AssignFriend")
    }
  }

  def postAssignFriend: Route = post {
    path("task" / "AssignFriend") {
      complete("post task/AssignFriend")
    }
  }
}


