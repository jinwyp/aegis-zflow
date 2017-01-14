package com.yimei.zflow.util.organ

import com.softwaremill.session.{MultiValueSessionSerializer, SessionConfig, SessionManager, SessionSerializer}
import com.softwaremill.session.SessionDirectives._
import com.softwaremill.session.SessionOptions._
import com.yimei.zflow.util.config.Core
import spray.json.DefaultJsonProtocol

import scala.util.Try

/**
  * Created by hary on 17/1/10.
  */

case class OrganSession(username: String,
                        userId: String,
                        party: String,
                        instanceId: String,
                        companyName: String)

trait Session extends DefaultJsonProtocol with Core {

  implicit val OrganSessionFormat = jsonFormat5(OrganSession)

  implicit val sessionManager = new SessionManager[OrganSession](SessionConfig.fromConfig(coreConfig))

  implicit def sessionSerializer: SessionSerializer[OrganSession, String] = new MultiValueSessionSerializer(
    (ms: OrganSession) => Map(
      "username" -> ms.username,
      "id" -> ms.userId,
      "party" -> ms.party,
      "instanceId" -> ms.instanceId,
      "companyName" -> ms.companyName),

    (msm: Map[String, String]) => Try {
      val gid = if (msm.get("gid").get == "") None else Some(msm.get("gid").get)
      OrganSession(
        msm.get("name").get,
        msm.get("id").get,
        msm.get("party").get,
        msm.get("instanceId").get,
        msm.get("companyName").get)
    }
  )

  def organSetSession(mySession: OrganSession) = setSession(oneOff, usingCookies, mySession)

  val organRequiredSession = requiredSession(oneOff, usingCookies)
  val organInvalidateSession = invalidateSession(oneOff, usingCookies)
}

