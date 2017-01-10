package com.yimei.zflow.money.utask

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.yimei.zflow.money.utask.Models._

/**
  * Created by hary on 17/1/10.
  */
trait TaskWriteFriendEvidence {
  def getWriteFriendEvidence: Route = get {
    path("task" / "WriteFriendEvidence") {
      complete("get task/WriteFriendEvidence")
    }
  }
  def postWriteFriendEvidence: Route = post {
    path("task" / "WriteFriendEvidence") {
      complete("post task/WriteFriendEvidence")
    }
  }
}

