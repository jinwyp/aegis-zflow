package com.yimei.zflow.engine.util.organ.routes

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.yimei.zflow.util.HttpResult._
import com.yimei.zflow.util.organ.OrganService
import com.yimei.zflow.util.organ.routes.Models.{UserAuthRequest, UserCreateRequest, UserSearchRequest}

trait UserRoute extends SprayJsonSupport with OrganService {

  /**
    * 创建用户
    * post /user/:party/:instance_id/:user_id  + json{name, username, password, email, phone}
    * @return
    */
  private def postUser: Route = post {
    path("user" / Segment / Segment / Segment) { (party, instanceId, userId) =>
      entity(as[UserCreateRequest]) { req =>
        complete(createUser(party, instanceId, userId, req))
      }
    }
  }

  /**
    * get /user/:party/instanceId/userId 查询用户
    * @return
    */
  private def getUser: Route = get {
    path("user" / Segment / Segment / Segment) { (party, instance_id, userId) =>
      complete(queryUser(party, instance_id, userId))
    }
  }

  /**
    * get /user/:party/:instanceId?page=10&pageSize=10
    * @return
    */
  private def getUserList: Route = get {
    pathPrefix("user" / Segment / Segment) { (party, instance_id) =>
      (parameter('page.as[Int]) & parameter('pageSize.as[Int])) { (page, pageSize) =>
        complete(getUserList(party, instance_id, page, pageSize))
      }
    }
  }


  /**
    * put /user/:party/:instance_id/:user_id  更新用户
    *
    * @return
    */
  private def putUser: Route = put {
    path("user" / Segment / Segment / Segment) { (party, instance_id, userId) =>
      entity(as[UserCreateRequest]) { user =>
        complete(updateUser(party, instance_id, userId, user))
      }
    }
  }

  /**
    * post /auth +json{username, password} 用户验证
    * @return
    */
  private def userAuth: Route = post {
    (path("auth") & entity(as[UserAuthRequest])) { user =>
      complete(auth(user))
    }
  }


  /**
    * 分页获取用户
    * POST /user?page=10&pageSize=10 + json{username, company}
    * @return
    */
  private def getAllUserInfo: Route = post {
    (path("user") & parameter('page.as[Int]) & parameter('pageSize.as[Int])) { (page, pageSize) =>
      entity(as[UserSearchRequest]) { req =>
        complete(search(req, page, pageSize))
      }
    }
  }

  /**
    * 禁用用户
    * POST /
    * @return
    */
  private def disableUser: Route = get {
    path("disable" / Segment) { userId =>
      complete(disable(userId))
    }
  }

  def userRoute: Route = postUser ~ getUser ~ putUser ~ getUserList ~ userAuth ~ disableUser ~ getAllUserInfo
}



