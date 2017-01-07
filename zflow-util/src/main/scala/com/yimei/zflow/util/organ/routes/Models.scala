package com.yimei.zflow.util.organ.routes

import spray.json.DefaultJsonProtocol

/**
  * Created by hary on 17/1/6.
  */
object Models extends DefaultJsonProtocol {


  // 用户创建model
  case class UserCreateRequest(password: String, phone: Option[String], email: Option[String], name: String, username: String)

  case class UserCreateResponse(status: String)

  implicit val UserCreateRequestFormat = jsonFormat5(UserCreateRequest)
  implicit val UserCreateResponseFormat = jsonFormat1(UserCreateResponse)

  // 用户查询
  case class UserQueryResponse(id: Long, party_id: Long, user_id: String, phone: Option[String], email: Option[String], name: String, username: String)

  implicit val UserQueryResponseFormat = jsonFormat7(UserQueryResponse)

  // 用户列表
  case class UserListResponse(userList: Seq[UserQueryResponse], total: Int)

  implicit val UserListResponseFormat = jsonFormat2(UserListResponse)

  // 用户authorization
  case class UserAuthRequest(username: String, password: String)
  case class UserAuthResponse(username: String, userId: String, email: Option[String], phone: Option[String], partyClass: String, instanceId: String, partyName: String)
  implicit val UserAuthRequestFormat = jsonFormat2(UserAuthRequest)
  implicit val UserAuthResponseFormat = jsonFormat7(UserAuthResponse)

  // 用户搜索
  case class UserSearchRequest(userName: Option[String], companyName: Option[String])
  case class UserSearchResponse()
  implicit val UserSearchRequestFormat = jsonFormat2(UserSearchRequest)
}
