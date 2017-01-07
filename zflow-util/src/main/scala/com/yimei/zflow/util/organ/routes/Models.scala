package com.yimei.zflow.util.organ.routes

import java.sql.Timestamp

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


  ///////////////////////////////////////////////////////////////////////////////////////////
  //
  ///////////////////////////////////////////////////////////////////////////////////////////
  case class PartyGroupEntry(id:Long,partyClass:String,gid:String,description:String)
  implicit val PartyGroupEntryResponseFormat = jsonFormat4(PartyGroupEntry)

  case class PartyGroupsResponse(partyGroups: Seq[PartyGroupEntry],total:Int)
  implicit val partyGroupsResponseFormat = jsonFormat2(PartyGroupsResponse)

  case class UserGroupEntry(id:Long,partyId:Long, gid:String, userId:String)
  implicit val UserGroupEntryFormat = jsonFormat4(UserGroupEntry)

  ///////////////////////////////////////////////////////////////////////////////////////////
  //
  ///////////////////////////////////////////////////////////////////////////////////////////
  case class PartyClassEntry(id: Long, className: String, description: String)
  implicit val PartyClassEntryFormat = jsonFormat3(PartyClassEntry)

  ///////////////////////////////////////////////////////////////////////////////////////////
  //
  ///////////////////////////////////////////////////////////////////////////////////////////
  case class PartyInstanceCreateRequest(party: String, instanceId: String, companyName: String)
  implicit val PartyInstanceCreateRequestFormat = jsonFormat3(PartyInstanceCreateRequest)

  case class PartyInstanceEntry(id: Long, partyClass: String, instanceId: String, companyName: String)
  implicit val PartyInstanceEntryFormat = jsonFormat4(PartyInstanceEntry)


  case class PartyInstancesResponse(partyInstanceList:Seq[PartyInstanceEntry],total:Int)
  implicit val PartyInstancesResponseFormat = jsonFormat2(PartyInstancesResponse)







}
