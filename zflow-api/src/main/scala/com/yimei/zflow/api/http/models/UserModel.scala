package com.yimei.zflow.api.http.models

import com.yimei.zflow.api.models.user.{State, UserProtocol}
import com.yimei.zflow.api.models.database.UserOrganizationDBModel._
import spray.json.DefaultJsonProtocol

/**
  * Created by xl on 16/12/23.
  */
object UserModel {

  case class UserInfo(password:String, phone:Option[String],email:Option[String], name:String, username: String)

  case class QueryUserResult(userInfo:PartyUserEntity,status:State)

  case class UserListEntity(userList:Seq[PartyUserEntity],total:Int)

  case class DynamicUserSearch(username: Option[String], name: Option[String], companyName: Option[String], partyClass: Option[String])

  case class UserLoginInfo(username: String, password: String)

  case class DynamicQueryUser(userName: Option[String], companyName: Option[String])

  trait UserModelProtocol extends DefaultJsonProtocol with UserProtocol {

    implicit val addUserModelFormat = jsonFormat5(UserInfo)
    implicit val queryUserResult = jsonFormat2(QueryUserResult)
    implicit val userlistFormat = jsonFormat2(UserListEntity)
    implicit val dynamicUserSearchFormat = jsonFormat4(DynamicUserSearch)
    implicit val userLoginInfoFormat = jsonFormat2(UserLoginInfo)
    implicit val dynamicQueryUserFormat = jsonFormat2(DynamicQueryUser)
  }
}
