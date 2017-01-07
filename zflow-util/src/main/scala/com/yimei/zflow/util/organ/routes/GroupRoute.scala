package com.yimei.zflow.engine.util.organ.routes

import java.sql.Timestamp
import java.time.Instant

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.yimei.zflow.util.organ.OrganService
import com.yimei.zflow.util.organ.db.Entities._

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
        complete()
      }
    }
  }

  //POST   /group/:party_class/:gid/description       创建参与方运营组
  def createGroupParty: Route = post {
    pathPrefix("group" / Segment / Segment / Segment) { (pc, gid, desc) =>
      val entity: Future[PartyGroupEntity] = dbrun(
        (partyGroup returning partyGroup.map(_.id)) into ((pg, tid) => pg.copy(id = tid)) += PartyGroupEntity(None, pc, gid, desc, Timestamp.from(Instant.now))
      )
      complete(entity)
    }
  }

  //DELETE /group/:party_class/:gid                    删除参与方运营组
  def deleteGroupParty: Route = delete {
    pathPrefix("group" / Segment / Segment) { (pc, gid) =>
      val delete = partyGroup.filter(pg => pg.party_class === pc && pg.gid === gid).delete
      val result = dbrun(delete) map { count =>
        if (count > 0) "success" else "fail"
      }
      complete(result)
    }
  }

  //PUT    /group/id/:party_class/:gid/:description                更新参与方运营组
  def updateGroupParty: Route = put {
    pathPrefix("group" / Segment / Segment / Segment / Segment) { (id, pc, gid, desc) =>
        val update = partyGroup.filter(_.id === id.toLong).map(p => (p.party_class, p.gid, p.description)).update(pc, gid, desc)
        val result = dbrun(update) map { count =>
          if(count > 0) "success" else "fail"
        }
        complete(result)
      }
  }


  def getUserByGroupAndParty  = get {
    pathPrefix("ugroup"/ Segment / Segment ) { (party_id,gid) =>
      val result: Future[Seq[UserGroupEntity]] = dbrun(userGroup.filter(u=>
          u.party_id === party_id.toLong   &&
          u.gid      === gid
      ).result)
      complete(result)
    }
  }

  def createUserGroup: Route = post {
    pathPrefix("ugroup" / Segment / Segment / Segment) { (party_id, gid, user_id) =>

      println("create user group begin")
      val entity: Future[UserGroupEntity] = dbrun(
        (userGroup returning userGroup.map(_.id)) into ((ug, id) => ug.copy(id = id)) += UserGroupEntity(None, party_id.toLong, gid, user_id, Timestamp.from(Instant.now))
      )
      complete(entity)
    }
  }

  /**
    * 判断该用户是否在改群组中
 *
    * @return
    */
  def userInGroup = get {
    pathPrefix("validateugroup"/Segment/Segment/Segment/Segment){ (party_class,instant_id,user_id,gid) =>

      val exist: Future[Seq[UserGroupEntity]] = dbrun((for{
        (pi,ug) <- partyInstance.filter(p=>
          p.party_class === party_class &&
          p.instance_id === instant_id
        ) join userGroup.filter( u=>
          u.user_id === user_id &&
          u.gid     === gid
        ) on(_.id === _.party_id)
      } yield {
        ug
      }).result)

      complete(exist)
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

/**
  * Created by hary on 16/12/2.
  */
object GroupRoute {

  implicit val userServiceTimeout = Timeout(2 seconds)

  def apply() = new GroupRoute()

  def route: Route = GroupRoute().route
}

