package com.yimei.zflow.util.organ.routes

/**
  * Created by hary on 16/12/19.
  */
import java.sql.Timestamp
import java.time.Instant

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.yimei.zflow.util.organ.db.Entities._
import com.yimei.zflow.util.organ.db.PartyInstanceTable

import scala.concurrent.Future

class InstRoute extends PartyInstanceTable with SprayJsonSupport{
  import driver.api._

  //POST /inst         创建参与方实例
  //body {party: String, instanceId: String, companyName: String}
  def createPartyInstance: Route = post {
    path("inst") {
      entity(as[PartyInstanceInfo]) { info =>

        val getPartyInstance: Future[Seq[PartyInstanceEntity]] = dbrun(
          partyInstance.filter( p =>
            p.disable === 0 &&
            p.instance_id === info.instanceId &&
            p.party_class === info.party
          ).result
        )

        def entity(pies: Seq[PartyInstanceEntity]): Future[PartyInstanceEntity] = {
          if(pies.length == 0) {
            dbrun(
              (partyInstance returning partyInstance.map(_.id)) into ((pi, id) => pi.copy(id = id)) += PartyInstanceEntity(None, info.party, info.instanceId, info.companyName, 0, Timestamp.from(Instant.now))
            )
          } else {
            throw BusinessException("已存在该公司")
          }

        }

       val result = for {
          pi <- getPartyInstance
          r <- entity(pi)
        } yield r

        complete(result)
      }
    }
  }

  //GET  /inst/:party/:instance_id           查询参与方实例
  def queryPartyInstance: Route = get {
    pathPrefix("inst" / Segment / Segment) { (pc, ii) =>
      complete(dbrun(partyInstance.filter(p => p.party_class === pc && p.instance_id === ii && p.disable === 0).result))
    }
  }

  //post  /inst/:party/:instance_id          更新参与方实例
  //body  companyName
  def updatePartyInstance: Route = put {
    path("inst" / Segment / Segment) { (party, ii) =>
      entity(as[String]) { companyName =>

        def getExistPartyInstance: Future[Seq[PartyInstanceEntity]] = dbrun(partyInstance.filter(p => p.instance_id === ii && p.disable === 0).result)

        def updatePartyInstance(pilist: Seq[PartyInstanceEntity]): Future[String] = {
          if(pilist.length == 1){
            dbrun(partyInstance.filter( p => p.instance_id === ii && p.disable === 0).map(p => (p.party_class, p.party_name)).update(party, companyName)) map { count =>
              if(count > 0) "success" else "fail"
            }
          }else{
            throw BusinessException("不存在对应的公司！")
          }
        }

        val result = for {
          pilist <- getExistPartyInstance
          re <- updatePartyInstance(pilist)
        } yield re

        complete(result)
      }
    }
  }

  //post /inst/list?page=x&pageSize=y
  //body Option[companyName]
  def getPartyInstanceList: Route = post {
    (path("inst" / "list") & parameter('page.as[Int]) & parameter('pageSize.as[Int])) { (page, pageSize) =>
      entity(as[Option[String]]) { companyName =>
        if(page <= 0 || pageSize <= 0)
          throw BusinessException("分页参数错误")

        val query = if(companyName.isDefined) {
          partyInstance.filter( pi =>
            pi.disable === 0
          ).filter{ pi =>
            pi.party_name like "%" + companyName.get + "%"
          }
        } else {
          partyInstance.filter{ pi =>
            pi.disable === 0
          }
        }

        def getPartyInstanceList: Future[Seq[PartyInstanceEntity]] = dbrun(query.drop((page - 1) * pageSize).take(pageSize).result)
        def getAccount = dbrun(query.length.result)

        val result = for {
          list <- getPartyInstanceList
          account <- getAccount
        } yield PartyInstanceListEntity(list, account)

        complete(result)
      }
    }
  }


  def route: Route = createPartyInstance ~ queryPartyInstance ~ updatePartyInstance ~ getPartyInstanceList
}

object InstRoute {
  def apply() = new InstRoute
  def route: Route = InstRoute().route
}