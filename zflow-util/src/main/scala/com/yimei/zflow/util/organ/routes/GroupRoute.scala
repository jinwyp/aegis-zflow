package com.yimei.zflow.engine.util.organ.routes

import java.sql.Timestamp
import java.time.Instant

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.yimei.zflow.util.organ.OrganService
import com.yimei.zflow.util.organ.db.Entities._
import com.yimei.zflow.util.ResultProtocol._

import scala.concurrent.Future
import scala.concurrent.duration._

trait GroupRoute extends OrganService with SprayJsonSupport {

  /**
    *
    * GET    /group/:party_class?limit=10&offset=20     参与方运营组列表
    * @return
    */
  def getGroupParty: Route = get {
    path("group" / Segment) { pc =>
      (parameter("limit".as[Int]) & parameter("offset".as[Int])) { (limit, offset) =>
        complete(getGroupsByParty(pc,limit,offset))
      }
    }
  }

  /**
    *
    * POST   /group/:party_class/:gid/description       创建参与方运营组
    * @return
    */
  def createGroupParty: Route = post {
    pathPrefix("group" / Segment / Segment / Segment) { (pc, gid, desc) =>
      complete(createGroupParty(pc,gid,desc))
    }
  }

  /**
    * /group/:party_class/:gid                    删除参与方运营组
    * @return
    */
  def deleteGroupParty: Route = delete {
    pathPrefix("group" / Segment / Segment) { (pc, gid) =>
      complete(deleteGroup(pc,gid))
    }
  }

  /**
    * PUT  /group/id/:party_class/:gid/:description                更新参与方运营组
    * @return
    */
  def updateGroupParty: Route = put {
    path("group" / LongNumber / Segment ) { (id, desc) =>
        complete(updateGroup(id,desc))
      }
  }


  /**
    *
    * @return
    */
  def getUserByGroupAndParty  = get {
    path("ugroup"/ LongNumber / Segment ) { (party_id, gid) =>
      complete(getUsersByGroup(party_id,gid))
    }
  }


  /**
    * 创建userGroup
    * @return
    */
  def createUserGroup: Route = post {
    pathPrefix("ugroup" / LongNumber / Segment / Segment) { (party_id, gid, user_id) =>
      complete(createUserGroup(party_id,gid,user_id))
    }
  }

  /**
    * 判断该用户是否在改群组中
 *
    * @return
    */
  def userInGroup = get {
    pathPrefix("validateugroup"/Segment/Segment/Segment/Segment){ (party_class,instant_id,user_id,gid) =>
      complete(auditUserInGroup(party_class,instant_id,user_id,gid))
    }
  }

  def route: Route =
    getGroupParty ~
      createGroupParty ~
      deleteGroupParty ~
      userInGroup ~
      updateGroupParty ~
      getUserByGroupAndParty ~
      createUserGroup
}


