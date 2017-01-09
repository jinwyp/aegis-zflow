package com.yimei.zflow.money.tasks

import akka.http.scaladsl.server.Route

/**
  * Created by hary on 17/1/9.
  */
object TaskRoute extends TaskApprove {
  def route: Route = approveRoute
}

