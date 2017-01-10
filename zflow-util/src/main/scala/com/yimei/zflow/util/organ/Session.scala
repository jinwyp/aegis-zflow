package com.yimei.zflow.util.organ

import com.softwaremill.session.{MultiValueSessionSerializer, SessionConfig, SessionManager, SessionSerializer}
import com.softwaremill.session.SessionDirectives._
import com.softwaremill.session.SessionOptions._
import spray.json.DefaultJsonProtocol

import scala.util.Try

/**
  * Created by hary on 17/1/10.
  */

case class OrganSession(userName: String,
                        userId: String,
                        party: String,
                        gid: Option[String],
                        instanceId: String,
                        companyName: String)

trait Session {
  implicit val sessionManager = new SessionManager[OrganSession](SessionConfig.fromConfig())

  implicit def sessionSerializer: SessionSerializer[OrganSession, String] = new MultiValueSessionSerializer(
    (ms: OrganSession) => Map(
      "name" -> ms.userName,
      "id" -> ms.userId,
      "party" -> ms.party,
      "gid" -> ms.gid.getOrElse(""),
      "instanceId" -> ms.instanceId,
      "companyName" -> ms.companyName),

    (msm: Map[String, String]) => Try {
      val gid = if (msm.get("gid").get == "") None else Some(msm.get("gid").get)
      OrganSession(
        msm.get("name").get,
        msm.get("id").get,
        msm.get("party").get,
        gid,
        msm.get("instanceId").get,
        msm.get("companyName").get)
    }
  )

  def organSetSession(mySession: OrganSession) = setSession(oneOff, usingCookies, mySession)

  val organRequiredSession = requiredSession(oneOff, usingCookies)
  val organInvalidateSession = invalidateSession(oneOff, usingCookies)
}

trait SessionProtocol extends DefaultJsonProtocol {
  implicit val OrganSessionFormat = jsonFormat6(OrganSession)
}
