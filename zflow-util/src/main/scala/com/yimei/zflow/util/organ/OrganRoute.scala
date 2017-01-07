package com.yimei.zflow.util.organ

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import com.yimei.zflow.engine.util.organ.routes.{GroupRoute, UserRoute}
import com.yimei.zflow.util.organ.routes.{InstRoute, PartyRoute}

/**
  * Created by wangqi on 17/1/4.
  */
object OrganRoute {
  def route(proxy:ActorRef)  = UserRoute.route(proxy) ~
    GroupRoute.route ~
    PartyRoute.route ~
    InstRoute.route
}
