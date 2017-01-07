package com.yimei.zflow.api.http.client

import com.yimei.zflow.api.http.models.UserModel._
import com.yimei.zflow.api.models.database.UserOrganizationDBModel.{PartyInstanceEntity, UserGroupEntity}
import com.yimei.zflow.api.models.user.State
import HttpUtil._
import com.yimei.cflow.graph.cang.models.UserModel.{UserData, UserInfoList}
import com.yimei.cflow.graph.cang.session.{MySession, SessionProtocol}
import com.yimei.zflow.engine.util.HttpUtil
import spray.json._

import scala.concurrent.Future

/**
  * Created by hary on 16/12/23.
  */
trait UserClient extends UserModelProtocol with SessionProtocol {
  def createPartyUser(party: String, instance_id: String, userId: String, userInfo: String): Future[State] = {
    //访问com.yimei.cflow.organ.routes.UserRoute中的postUser接口
    sendRequest(
      path = "api/user",
      pathVariables = Array(party, instance_id, userId),
      method = "post",
      bodyEntity = Some(userInfo)) map { result =>
      result.parseJson.convertTo[State]
    }
  }

  def createUserGroup(party_id: String, gid: String, user_id: String): Future[UserGroupEntity] = {
    //访问com.yimei.cflow.organ.routes.GroupRoute中的createUserGroup接口
    sendRequest(
      path = "api/ugroup",
      pathVariables = Array(party_id, gid, user_id),
      method = "post"
    ) map { result =>
    result.parseJson.convertTo[UserGroupEntity]
    }
  }

  def updatePartyUser(party: String, instance_id: String, userId: String, userInfo: String): Future[String] = {
    //访问com.yimei.cflow.organ.routes.UserRoute中的putUser接口
    sendRequest(
      path = "api/user",
      pathVariables = Array(party, instance_id, userId),
      method = "put",
      bodyEntity = Some(userInfo)
    )
  }

  def getSpecificPartyUser(party: String, instance_id: String, userId: String): Future[QueryUserResult] = {
    //访问com.yimei.cflow.organ.routes.UserRoute中的getUser接口
    sendRequest(
      path = "api/user",
      pathVariables = Array(party, instance_id, userId),
      method = "get"
    ) map { result =>
      result.parseJson.convertTo[QueryUserResult]
    }
  }

  def getLoginUserInfo(userInfo: String): Future[MySession] = {
    //访问com.yimei.cflow.organ.routes.UserRoute中的getLoginUserInfo接口
    sendRequest(
      path = "api/login",
      method = "post",
      bodyEntity = Some(userInfo)
    ) map { result =>
      result.parseJson.convertTo[MySession]
    }
  }

  def getUserList(party: String, instance_id: String, limit: Int, offset: Int): Future[UserListEntity] = {
    //访问com.yimei.cflow.organ.routes.UserRoute中的getUserList接口
    sendRequest(
      path = "api/user",
      paramters = Map("limit" -> limit.toString, "offset" -> offset.toString),
      pathVariables = Array(party, instance_id),
      method = "get"
    ) map { result =>
      result.parseJson.convertTo[UserListEntity]
    }
  }

  def disableUser(userId: String): Future[String] = {
    //访问com.yimei.cflow.organ.routes.UserRoute中的disAbleUser接口
    sendRequest(
      path = "api/disable",
      pathVariables = Array(userId),
      method = "get"
    )
  }

  def getAllUserList(page: Int, pageSize: Int, dynamicQuery: String): Future[UserInfoList] = {
    //访问com.yimei.cflow.organ.routes.UserRoute中的getAllUserInfo接口
    sendRequest(
      path = "api/alluser",
      paramters = Map("page" -> page.toString, "pageSize" -> pageSize.toString),
      method = "post",
      bodyEntity = Some(dynamicQuery)
    ) map { result =>
      result.parseJson.convertTo[UserInfoList]
    }
  }
}
