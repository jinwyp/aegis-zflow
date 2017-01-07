package com.yimei.zflow.util.organ

import java.sql.Timestamp
import java.time.Instant

import com.sun.media.sound.SoftMidiAudioFileReader
import com.yimei.zflow.util.ResultProtocol.Result
import com.yimei.zflow.util.config.CoreConfig
import com.yimei.zflow.util.exception.{BusinessException, DatabaseException}
import com.yimei.zflow.util.organ.db.Entities._
import com.yimei.zflow.util.organ.db._
import com.yimei.zflow.util.organ.routes.Models._

import scala.collection.mutable
import scala.concurrent.Future

/**
  * Created by hary on 16/12/29.
  */
trait OrganService extends CoreConfig
  with PartyGroupTable
  with PartyInstanceTable
  with PartyUserTable
  with UserGroupTable
  with PartyClassTable {

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
  def search(req: UserSearchRequest, page: Int, pageSize: Int): Future[Result[UserGroupListResponse]] = {

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

    def getResult(info: (String, String, String, String, String, String, String, String)): UserGroupQueryResponse = UserGroupQueryResponse(
      info._1, info._2, info._3, info._4, info._5, info._6, info._7, info._8
    )

    val userName = "%" + req.userName.getOrElse("") + "%"
    val companyName = "%" + req.companyName.getOrElse("") + "%"
    for {
      info <- getUserInfo(userName, companyName, pageSize, (page - 1) * pageSize)
      total <- getAccount(userName, companyName)
    } yield Result[UserGroupListResponse](Some(UserGroupListResponse(userGroupList = info.map(getResult(_)), total = total)), success = true)
  }

  private def toPartyGroupEntry(pt: PartyGroupEntity): PartyGroupEntry = PartyGroupEntry(pt.id.get, pt.party_class, pt.gid, pt.description)

  /**
    *
    * @param partyClass
    * @param limit
    * @param offset
    * @return
    */
  def getGroupsByParty(partyClass: String, limit: Int, offset: Int): Future[Result[PartyGroupsResponse]] = {
    val rs: Future[Seq[PartyGroupEntity]] = dbrun(partyGroup.filter(_.party_class === partyClass).drop(offset).take(limit).result)
    val total: Future[Int] = dbrun(partyGroup.filter(_.party_class === partyClass).length.result)

    for {
      r: Seq[PartyGroupEntity] <- rs
      t: Int <- total
    } yield Result(Some(
      PartyGroupsResponse(r.map(toPartyGroupEntry(_)), t))
    )
  }

  /**
    * 创建group
    *
    * @param partyClass
    * @param gid
    * @param desc
    * @return
    */
  def createGroupParty(partyClass: String, gid: String, desc: String): Future[Result[PartyGroupEntry]] = {
    dbrun(
      (partyGroup returning partyGroup.map(_.id)) into ((pg, tid) => pg.copy(id = tid)) += PartyGroupEntity(None, partyClass, gid, desc, Timestamp.from(Instant.now))
    ) map { pt =>
      Result(Some(toPartyGroupEntry(pt)))
    }
  }


  /**
    * 删除group
    *
    * @param partyClass
    * @param gid
    * @return
    */
  def deleteGroup(partyClass: String, gid: String): Future[Result[String]] = {
    val delete = partyGroup.filter(pg => pg.party_class === partyClass && pg.gid === gid).delete
    dbrun(delete) map { count =>
      if (count > 0) Result(Some("success")) else throw DatabaseException(s"$partyClass,$gid 删除失败")
    }
  }


  /**
    * 更新group描述
    *
    * @param id
    * @param desc
    * @return
    */
  def updateGroup(id: Long, desc: String): Future[Result[String]] = {
    val update = partyGroup.filter(_.id === id).map(p => p.description).update(desc)
    dbrun(update) map { count =>
      if (count > 0) Result(Some("success")) else throw DatabaseException(s"$id 更新失败")
    }
  }


  private def toUserGroupEntry(ug: UserGroupEntity) = UserGroupEntry(ug.id.get, ug.party_id, ug.gid, ug.user_id)

  /**
    * 根据partyId和gid查找用户组
    *
    * @param partyId
    * @param gid
    * @return
    */
  def getUsersByGroup(partyId: Long, gid: String): Future[Result[Seq[UserGroupEntry]]] = {
    dbrun(userGroup.filter(u =>
      u.party_id === partyId &&
        u.gid === gid
    ).result) map { sq =>
      Result(Some(sq.map(toUserGroupEntry(_))))
    }
  }


  /**
    * 创建用户组
    *
    * @param partyId
    * @param gid
    * @param userId
    * @return
    */
  def createUserGroup(partyId: Long, gid: String, userId: String): Future[Result[UserGroupEntry]] = {
    dbrun(
      (userGroup returning userGroup.map(_.id)) into ((ug, id) => ug.copy(id = id)) += UserGroupEntity(None, partyId, gid, userId, Timestamp.from(Instant.now))
    ) map { ug =>
      Result(Some(toUserGroupEntry(ug)))
    }
  }

  /**
    * 判断该用户是否在group里
    * @param partyClass
    * @param instantId
    * @param userId
    * @param gid
    * @return
    */
  def auditUserInGroup(partyClass: String, instantId: String, userId: String, gid: String): Future[Result[Seq[UserGroupEntry]]] = {
    dbrun((for{
      (pi,ug) <- partyInstance.filter(p=>
        p.party_class === partyClass &&
          p.instance_id === instantId
      ) join userGroup.filter( u=>
        u.user_id === userId &&
          u.gid     === gid
      ) on(_.id === _.party_id)
    } yield {
      ug
    }).result) map { sq =>
      Result(Some(sq.map(toUserGroupEntry(_))))
    }
  }


  private def toPartyClassEntry(pc:PartyClassEntity) = PartyClassEntry(pc.id.get, pc.class_name, pc.description)

  /**
    *
    * @param offset
    * @param limit
    * @return
    */
  def getParties(offset: Int, limit: Int): Future[Result[Seq[PartyClassEntry]]] =  {
    dbrun(partClass.drop(offset).take(limit).result) map { t =>
      Result(Some(t.map(toPartyClassEntry(_))))
    }
  }


  /**
    * 创建party_class
    * @param partyClass
    * @param desc
    * @return
    */
  def createParty(partyClass:String, desc:String): Future[Result[PartyClassEntry]] = {
   dbrun(
      (partClass returning partClass.map(_.id)) into ((party, id) => party.copy(id = id)) += PartyClassEntity(None, partyClass, desc)
    ) map { pt =>
     Result(Some(toPartyClassEntry(pt)))
   }
  }


  /**
    * 查询party
    *
    * @param partyClass
    * @return
    */
  def queryParty(partyClass:String): Future[Result[Seq[PartyClassEntry]]] = {
    dbrun(partClass.filter(p => p.class_name === partyClass).result) map { (pt: Seq[PartyClassEntity]) =>
      Result(Some(pt.map(toPartyClassEntry(_))))
    }
  }

  /**
    * 更新party_class
    * @param id
    * @param desc
    * @return
    */
  def updateParty(id:Long,desc:String): Future[Result[String]] = {
    val update = partClass.filter(_.id === id).map(p => p.description).update(desc)
    dbrun(update) map { count =>
      if (count > 0) Result(Some("success")) else throw DatabaseException(s"$id,$desc party_class更新失败")
    }
  }



  private def toPartyInstanceEntry(pt:PartyInstanceEntity) = PartyInstanceEntry(pt.id.get,pt.partyClass,pt.instanceId,pt.companyName)

  /**
    *创建公司
    * @param info
    */
  def createPartyInstance(info:PartyInstanceCreateRequest): Future[Result[PartyInstanceEntry]] = {
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

    for {
      pi <- getPartyInstance
      r <- entity(pi)
    } yield Result(Some(toPartyInstanceEntry(r)))
  }

  /**
    * 查询公司
    *
    * @param partyClass
    * @param instanceId
    * @return
    */
  def queryPartyInstance(partyClass:String,instanceId:String): Future[Result[Seq[PartyInstanceEntry]]] = {
    dbrun(partyInstance.filter(p => p.party_class === partyClass && p.instance_id === instanceId && p.disable === 0).result) map { r=>
      Result(Some(r.map(toPartyInstanceEntry(_))))
    }
  }


  /**
    *更新公司
    * @param partyClass
    * @param instanceId
    * @param companyName
    */
  def updatePartyInstance(partyClass:String,instanceId:String,companyName:String): Future[Result[String]] = {
    def getExistPartyInstance: Future[Seq[PartyInstanceEntity]] = dbrun(partyInstance.filter(p => p.instance_id === instanceId && p.disable === 0).result)

    def updatePartyInstance(pilist: Seq[PartyInstanceEntity]): Future[Result[String]] = {
      if(pilist.length == 1){
        dbrun(partyInstance.filter( p => p.instance_id === instanceId && p.disable === 0).map(p => (p.party_class, p.party_name)).update(partyClass, companyName)) map { count =>
          if(count > 0) Result(Some("success")) else throw DatabaseException(s"$partyClass, $instanceId, $companyName 更新错误")
        }
      }else{
        throw BusinessException("不存在对应的公司！")
      }
    }

    for {
      pilist <- getExistPartyInstance
      re <- updatePartyInstance(pilist)
    } yield re
  }


  /**
    * 获得公司列表
 *
    * @param page
    * @param pageSize
    * @param companyName
    */
  def getPartyInstanceList(page:Int, pageSize:Int, companyName:Option[String]): Future[Result[PartyInstancesResponse]] = {
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

    for {
      list <- getPartyInstanceList
      account <- getAccount
    } yield Result(Some(PartyInstancesResponse(list.map(toPartyInstanceEntry(_)), account)))
  }

}
