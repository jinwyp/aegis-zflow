package com.yimei.zflow.engine.util.organ.routes

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.yimei.zflow.util.HttpResult._
import com.yimei.zflow.util.organ.OrganService

trait GroupRoute extends OrganService with SprayJsonSupport {

  /**
    * 获取参与方类别运营组列表
    * GET  /group/:party_class?page=10&pageSize=20
    * @return
    */
  private def getGroupParty: Route = get {
    path("group" / Segment) { pc =>
      (parameter("page".as[Int]) & parameter("pageSize".as[Int])) { (page, pageSize) =>
        complete(getGroupsByParty(pc, page, pageSize))
      }
    }
  }

  /**
    * 创建参与方运营组
    * POST   /group/:party_class/:gid/description
    *
    * @return
    */
  private def createGroupParty: Route = post {
    pathPrefix("group" / Segment / Segment / Segment) { (pc, gid, desc) =>
      complete(createGroupParty(pc, gid, desc))
    }
  }

  /**
    * @deprecated
    *
    * 删除参与方运营组
    * /group/:party_class/:gid
    * @return
    */
  private def deleteGroupParty: Route = delete {
    pathPrefix("group" / Segment / Segment) { (pc, gid) =>
      complete(deleteGroup(pc, gid))
    }
  }

  /**
    * 更新参与方运营组
    * PUT  /group/:id/:party_class/:gid/:description
    * @return
    */
  private def updateGroupParty: Route = put {
    path("group" / LongNumber / Segment) { (id, desc) =>
      complete(updateGroup(id, desc))
    }
  }


  /**
    * 依据用户参与方实例id, 运营组获取这个运营组的用户列表
    * GET /ugroup/:party_instance_id/:gid
    * @return
    */
  private def getUserByGroupAndParty = get {
    path("ugroup" / LongNumber / Segment) { (party_id, gid) =>
      complete(getUsersByGroup(party_id, gid))
    }
  }


  /**
    * 创建userGroup  参与方实例(公司)的user_id加入运营组gid
    *
    * POST /ugroup/:party_instance_id/:gid/:user_id
    * @return
    */
  private def createUserGroup: Route = post {
    path("ugroup" / LongNumber / Segment / Segment) { (party_id, gid, user_id) =>
      complete(createUserGroup(party_id, gid, user_id))
    }
  }

  /**
    * 判断该用户是否在运营组中
    * GET /validateugroup/:party_class/:instance_id/:user_id/:gid
    * @return
    */
  private def userInGroup = get {
    path("validateugroup" / Segment / Segment / Segment / Segment) { (party_class, instant_id, user_id, gid) =>
      complete(auditUserInGroup(party_class, instant_id, user_id, gid))
    }
  }

  def groupRoute: Route =
    getGroupParty ~
      createGroupParty ~
      deleteGroupParty ~
      userInGroup ~
      updateGroupParty ~
      getUserByGroupAndParty ~
      createUserGroup
}


