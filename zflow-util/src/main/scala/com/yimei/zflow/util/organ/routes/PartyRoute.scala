package com.yimei.zflow.util.organ.routes

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.yimei.zflow.util.organ.OrganService
import com.yimei.zflow.util.ResultProtocol._

/**
  * Created by hary on 16/12/19.
  */
trait PartyRoute extends OrganService with SprayJsonSupport {

  /**
    * GET  /party?limit=10&offset=20         参与方类别列表
    * @return
    */
  def getParty: Route = get {
    (path("party") & parameter("limit".as[Int]) & parameter("offset".as[Int])) { (limit, offset) =>
      complete(getParties(limit,offset))
    }
  }

  /**
    * POST /party/:className/:description        创建参与方类别
    * @return
    */
  def partyCreate: Route = post {
    path("party" / Segment / Segment) { (pc, pd) =>
      complete(createParty(pc,pd))
    }
  }

  //GET  /party/:className                     查询参与方类别
  def partyQuery: Route = get {
    path("party" / Segment) { pc =>
      complete(queryParty(pc))
    }
  }

  //PUT  /party/:id/:description    更新参与方类别

  def updatePt: Route = put {
    path("party" / LongNumber  / Segment) { (id , pd) =>
      complete(updateParty(id,pd))
    }
  }

  def partyRoute: Route = getParty ~ partyCreate ~ partyQuery ~ updatePt

}

