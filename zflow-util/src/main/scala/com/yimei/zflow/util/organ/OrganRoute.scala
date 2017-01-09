package com.yimei.zflow.util.organ

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.yimei.zflow.engine.util.organ.routes.{GroupRoute, UserRoute}
import com.yimei.zflow.util.organ.routes.{InstRoute, PartyRoute}

/**
  * Created by wangqi on 17/1/4.
  */
trait OrganRoute extends GroupRoute with UserRoute with PartyRoute with InstRoute  {

  def organRoute: Route = groupRoute ~ userRoute ~ partyRoute ~ instRoute

}

