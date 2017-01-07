package com.yimei.zflow.engine.util.organ.routes

import java.util.UUID

import akka.actor.ActorRef
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.yimei.zflow.util.organ.OrganService
import com.yimei.zflow.util.organ.routes.Models.{UserAuthRequest, UserCreateRequest, UserSearchRequest}
import com.yimei.zflow.util.ResultProtocol._

import scala.concurrent.Future

trait UserRoute extends SprayJsonSupport with OrganService {

  /**
    *  创建用户
    * @return
    */
  def postUser: Route = post {
    path("user" / Segment / Segment / Segment) { (party, instance_id, userId) =>
      entity(as[UserCreateRequest]) { req =>
        complete(createUser(party, instance_id, userId, req))
      }
    }
  }

  /**
    *  查询用户
    * @return
    */
  def getUser: Route = get {
    path("user" / Segment / Segment / Segment) { (party, instance_id, userId) =>
      complete(queryUser(party, instance_id, userId))
    }
  }

  /**
    *
    * @return
    */
  def getUserList: Route = get {
    pathPrefix("user" / Segment / Segment) { (party, instance_id) =>
      (parameter('limit.as[Int]) & parameter('offset.as[Int])) { (limit, offset) =>
        complete(getUserList(party, instance_id, limit, offset))
      }
    }
  }


  /**
    * 更新用户
    *
    * @return
    */
  def putUser: Route = put {
    path("user" / Segment / Segment / Segment) { (party, instance_id, userId) =>
      entity(as[UserCreateRequest]) { user =>
        complete(updateUser(party, instance_id, userId, user))
      }
    }
  }

  /**
    * 用户验证
    * @return
    */
  def userAuth: Route = post {
    (path("auth") & entity(as[UserAuthRequest])) { user =>
      complete(auth(user))
    }
  }


  /**
    *
    * @return
    */
  def getAllUserInfo: Route = post {
    (path("alluser") & parameter('page.as[Int]) & parameter('pageSize.as[Int])) { (page, pageSize) =>
      entity(as[UserSearchRequest]) { req =>
        complete(search(req, page, pageSize))
      }
    }
  }

  /**
    *
    * @return
    */
  def disableUser: Route = get {
    path("disable" / Segment) { userId =>
      complete(disable(userId))
    }
  }

  def userRoute: Route = postUser ~ getUser ~ putUser ~ getUserList ~ userAuth ~ disableUser ~ getAllUserInfo
}



