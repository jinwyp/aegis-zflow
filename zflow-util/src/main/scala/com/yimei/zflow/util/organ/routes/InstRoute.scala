package com.yimei.zflow.util.organ.routes

/**
  * Created by hary on 16/12/19.
  */
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.yimei.zflow.util.HttpResult._
import com.yimei.zflow.util.organ.OrganService
import com.yimei.zflow.util.organ.routes.Models.PartyInstanceCreateRequest

trait InstRoute extends OrganService with SprayJsonSupport{

  //POST /inst         创建参与方实例
  //body {party: String, instanceId: String, companyName: String}
  private def createPti: Route = post {
    path("inst") {
      entity(as[PartyInstanceCreateRequest]) { info =>
        complete(createPartyInstance(info))
      }
    }
  }

  //GET  /inst/:party/:instance_id           查询参与方实例
  private def queryPti: Route = get {
    pathPrefix("inst" / Segment / Segment) { (pc, ii) =>
      complete(queryPartyInstance(pc,ii))
    }
  }

  //post  /inst/:party/:instance_id          更新参与方实例
  //body  companyName
  private def updatePti: Route = put {
    path("inst" / Segment / Segment) { (party, ii) =>
      entity(as[String]) { companyName =>
        complete(updatePartyInstance(party,ii,companyName))
      }
    }
  }

  //post /inst/list?page=x&pageSize=y
  //body Option[companyName]
  private def getPartyInstanceList: Route = post {
    (path("inst" / "list") & parameter('page.as[Int]) & parameter('pageSize.as[Int])) { (page, pageSize) =>
      entity(as[Option[String]]) { companyName =>
        complete(getPartyInstanceList(page,pageSize,companyName))
      }
    }
  }


  def instRoute: Route = createPti ~ queryPti ~ updatePti ~ getPartyInstanceList
}
