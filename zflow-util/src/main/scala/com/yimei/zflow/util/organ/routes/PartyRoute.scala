package com.yimei.zflow.util.organ.routes

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.yimei.zflow.util.organ.db.Entities._
import com.yimei.zflow.util.organ.db.PartyClassTable

import scala.concurrent.Future

/**
  * Created by hary on 16/12/19.
  */
class PartyRoute extends PartyClassTable with SprayJsonSupport {

  import driver.api._

  //GET  /party?limit=10&offset=20         参与方类别列表
  def getParty: Route = get {
    (pathPrefix("party") & parameter("limit".as[Int]) & parameter("offset".as[Int])) { (limit, offset) =>
      complete(dbrun(partClass.drop(offset).take(limit).result))
    }
  }

  //POST /party/:className/:description        创建参与方类别
  def createParty: Route = post {
    pathPrefix("party" / Segment / Segment) { (pc, pd) =>
      val entity: Future[PartyClassEntity] = dbrun(
        (partClass returning partClass.map(_.id)) into ((party, id) => party.copy(id = id)) += PartyClassEntity(None, pc, pd)
      )
      complete(entity)
    }
  }

  //GET  /party/:className                     查询参与方类别
  def queryParty: Route = get {
    pathPrefix("party" / Segment) { pc =>
      complete(dbrun(partClass.filter(p => p.class_name === pc).result))
    }
  }

  //PUT  /party/:id/:className/:description    更新参与方类别

  def updateParty: Route = put {
    pathPrefix("party" / Segment / Segment / Segment) { (id, pc, pd) =>
      val update = partClass.filter(_.id === id.toLong).map(p => (p.class_name, p.description)).update(pc, pd)
      val result = dbrun(update) map { count =>
        if (count > 0) "success" else "fail"
      }
      complete(result)
    }
  }

  def route: Route = getParty ~ createParty ~ queryParty ~ updateParty
}

object PartyRoute {
  def apply() = new PartyRoute

  def route: Route = PartyRoute().route
}
