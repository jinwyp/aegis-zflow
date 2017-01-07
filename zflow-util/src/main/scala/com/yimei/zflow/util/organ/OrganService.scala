package com.yimei.zflow.util.organ

import java.sql.Timestamp
import java.time.Instant

import akka.http.scaladsl.server.Directives._
import com.yimei.zflow.util.ResultProtocol.Result
import com.yimei.zflow.util.config.CoreConfig
import com.yimei.zflow.util.exception.{BusinessException, DatabaseException}
import com.yimei.zflow.util.organ.db.Entities.{PartyInstanceEntity, PartyUserEntity}
import com.yimei.zflow.util.organ.db._
import com.yimei.zflow.util.organ.routes.Models.{UserAuthRequest, UserAuthResponse, UserCreateRequest, UserCreateResponse, UserListResponse, UserQueryResponse, UserSearchRequest}

import scala.collection.mutable
import scala.concurrent.Future

/**
  * Created by hary on 16/12/29.
  */
trait OrganService extends CoreConfig
  with PartyGroupTable
  with PartyInstanceTable
  with PartyUserTable
  with UserGroupTable {

  import driver.api._

  /**
    * 创建用户
    *
    * @param party
    * @param instance_id
    * @param userId
    * @param user
    */
  def createUser(party: String, instance_id: String, userId: String, user: UserCreateRequest) = {

    def pi: Future[PartyInstanceEntity] = dbrun(partyInstance.filter(p =>
      p.party_class === party &&
        p.instance_id === instance_id
    ).result.head) recover {
      case _ => throw new DatabaseException("不存在该公司")
    }

    def insert(p: PartyInstanceEntity): Future[PartyUserEntity] = {
      dbrun(partyUser returning partyUser.map(_.id) into ((pu, id) => pu.copy(id = id)) +=
        PartyUserEntity(None, p.id.get, userId, user.password, user.phone, user.email, user.name, user.username, 0, Timestamp.from(Instant.now))) recover {
        case e =>
          log.error("{}", e)
          throw new DatabaseException("添加用户错误")
        //case a:SQLIntegrityConstraintViolationException => PartyUserEntity(None,p.id.get,userId,user.password,user.phone,user.email,user.name,Timestamp.from(Instant.now))
      }
    }

    for {
      p <- pi
      u <- insert(p)
    } yield Result(Some(UserCreateResponse("success")))

  }

  /**
    *
    * @param party
    * @param instance_id
    * @param userId
    */
  def queryUser(party: String, instance_id: String, userId: String) = {
    def pi: Future[PartyInstanceEntity] = dbrun(partyInstance.filter(p =>
      p.party_class === party &&
        p.instance_id === instance_id
    ).result.head) recover {
      case _ => throw new DatabaseException("不存在该公司")
    }

    def getUser(p: PartyInstanceEntity): Future[PartyUserEntity] = {
      dbrun(partyUser.filter(u =>
        u.user_id === userId &&
          u.party_id === p.id &&
          u.disable === 0
      ).result.head) recover {
        case _ => throw new DatabaseException("不存在该用户")
      }
    }

    for {
      p <- pi
      u <- getUser(p)
    } yield Result(Some(getUserQueryResponse(u)))
  }


  private def getUserQueryResponse(u: PartyUserEntity) = UserQueryResponse(
    id = u.id.get,
    party_id = u.party_id,
    user_id = u.user_id,
    phone = u.phone,
    email = u.email,
    name = u.name,
    username = u.username
  )

  /**
    *
    * @param party
    * @param instance_id
    * @param limit
    * @param offset
    */
  def getUserList(party: String, instance_id: String, limit: Int, offset: Int) = {
    val pi: Future[PartyInstanceEntity] = dbrun(partyInstance.filter(p =>
      p.party_class === party &&
        p.instance_id === instance_id
    ).result.head) recover {
      case _ => throw new DatabaseException("不存在该公司")
    }

    def getUserList(p: PartyInstanceEntity): Future[Seq[PartyUserEntity]] = {
      dbrun(partyUser.filter(u =>
        u.party_id === p.id &&
          u.disable === 0
      ).drop(offset).take(limit).result) recover {
        case _ => throw new DatabaseException("不存在该用户")
      }
    }

    def getTotal(p: PartyInstanceEntity) = {
      dbrun(partyUser.filter(u =>
        u.party_id === p.id &&
          u.disable === 0
      ).length.result) recover {
        case _ => throw new DatabaseException("不存在该用户")
      }
    }

    for {
      p <- pi
      u <- getUserList(p)
      total <- getTotal(p)
    } yield Result(Some(UserListResponse(u.map(getUserQueryResponse(_)), total)))
  }

  def updateUser(party: String, instance_id: String, userId: String, user: UserCreateRequest) = {

    def pi: Future[PartyInstanceEntity] = dbrun(partyInstance.filter(p =>
      p.party_class === party &&
        p.instance_id === instance_id
    ).result.head) recover {
      case _ => throw new DatabaseException("不存在该公司")
    }

    def update(p: PartyInstanceEntity) = {

      val pu = partyUser.filter(u =>
        u.user_id === userId &&
          u.party_id === p.id
      ).map(t => (t.password, t.phone, t.email, t.name, t.username)).update(
        user.password, user.phone, user.email, user.name, user.username
      )

      dbrun(pu) map { i =>
        i match {
          case 1 => Result(Some("success"))
          case _ => throw DatabaseException(s"$party , $instance_id, $userId, $user 更新失败 ")
        }
      } recover {
        case e =>
          log.error("{}", e)
          throw DatabaseException(s"$party , $instance_id, $userId, $user 更新失败 ")
      }
    }

    for {
      p <- pi
      r <- update(p)
    } yield r
  }

  /**
    *
    * @param user
    */
  def auth(user: UserAuthRequest) = {
    val query =
      (for {
        (pu, pi) <- partyUser join partyInstance on (_.party_id === _.id) if (pu.username === user.username && pu.password === user.password && pu.disable === 0)
      } yield (
        pu.username,
        pu.user_id,
        pu.email,
        pu.phone,
        pi.party_class,
        pi.instance_id,
        pi.party_name)).result.head

    for {
      info <- dbrun(query)
    } yield Result(Some(UserAuthResponse(
      username = info._1,
      userId = info._2,
      email = info._3,
      phone = info._4,
      partyClass = info._5,
      instanceId = info._6,
      partyName = info._7))
    )
  }

  /**
    *
    * @param userId
    * @return
    */
  def disable(userId: String): Future[Result[String]] = {

    val pu = partyUser.filter(u =>
      u.user_id === userId &&
        u.disable === 0
    ).map(t => (t.disable)).update(1)

    dbrun(pu) map { i =>
      i match {
        case 1 => Result(Some("success"))
        case _ => throw BusinessException(s"$userId 禁用失败")
      }
    } recover {
      case _ => throw BusinessException(s"$userId 禁用失败")
    }
  }

  /**
    *
    * @param req
    * @param page
    * @param pageSize
    */
  def search(req: UserSearchRequest, page: Int, pageSize: Int): = {

    if (page <= 0 || pageSize <= 0) throw BusinessException("分页参数有误！")

    def getUserInfo(userName: String, companyName: String, l: Int, os: Int) = {
      val query =
        sql"""
             select pu.user_id, pu.username, pu.email, pu.phone, pi.party_class, ug.gid, pi.instance_id, pi.party_name
             from party_instance pi join party_user pu on pu.party_id = pi.id
             left join user_group ug on pu.user_id = ug.user_id
             where pi.party_name like $companyName and pu.username like $userName limit $l offset $os
             """
      dbrun(query.as[(String, String, String, String, String, String, String, String)])
    }

    def getAccount(userName: String, companyName: String): Future[Int] = {
      val query =
        sql"""
             select count(1)
             from party_instance pi join party_user pu on pu.party_id = pi.id
             left join user_group ug on pu.user_id = ug.user_id
             where pi.party_name like $companyName and pu.username like $userName
             """
      for {
        account <- dbrun(query.as[Int])
      } yield account.toList.head

    }

    def getResult(Info: Seq[(String, String, String, String, String, String, String, String)]): List[UserData] = {
      var result = mutable.MutableList[UserData]()
      Info.toList.foreach { info =>
        val role = if (info._6 == null || info._6 == "1") info._5 else info._5 + "Accountant"
        result += UserData(info._1, info._2, info._3, info._4, role, info._7, info._8)
      }
      result.toList
    }

    val userName = "%" + qi.userName.getOrElse("") + "%"
    val companyName = "%" + qi.companyName.getOrElse("") + "%"
    val result = for {
      info <- getUserInfo(userName, companyName, pageSize, (page - 1) * pageSize)
      list = getResult(info)
      total <- getAccount(userName, companyName)
    } yield UserInfoList(datas = list, total = total)


  }

  /**
    *
    * @param partyClass
    * @param limit
    * @param offset
    * @return
    */
  def getGroupsByParty(partyClass: String, limit: Int, offset: Int) = {
    dbrun(partyGroup.filter(_.party_class === partyClass).drop(offset).take(limit).result)
  }

}
