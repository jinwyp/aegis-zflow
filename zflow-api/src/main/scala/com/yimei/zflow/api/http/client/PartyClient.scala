package com.yimei.zflow.api.http.client

import scala.concurrent.Future
import HttpUtil._
import akka.event.{Logging, LoggingAdapter}
import com.yimei.zflow.api.http.models.PartyModel.{PartyInstanceListEntity, PartyModelProtocol}
import com.yimei.zflow.api.models.database.UserOrganizationDBModel.PartyInstanceEntity
import com.yimei.zflow.api.models.user.UserProtocol
import com.yimei.zflow.engine.util.HttpUtil
import spray.json._

/**
  * Created by hary on 16/12/23.
  */
trait PartyClient extends UserProtocol with PartyModelProtocol{
  def createPartyInstance(partyInfo: String): Future[PartyInstanceEntity] = {
    //访问com.yimei.cflow.organ.routes.InstRoute中的createPartyInstance接口
    sendRequest(
      path = "api/inst",
      method = "post",
      bodyEntity = Some(partyInfo)) map { result =>
      result.parseJson.convertTo[PartyInstanceEntity]
    }
  }

  def queryPartyInstance(party_class: String, instance_id: String): Future[List[PartyInstanceEntity]] = {
    //访问com.yimei.cflow.organ.routes.InstRoute中的queryPartyInstance接口
    sendRequest(
      path = "api/inst",
      pathVariables = Array(party_class, instance_id),
      method = "get") map { result =>
      result.parseJson.convertTo[List[PartyInstanceEntity]]
    }
  }

  def getAllPartyInstanceList(page: Int, pageSize: Int, companyName: Option[String]): Future[PartyInstanceListEntity] = {
    //访问com.yimei.cflow.organ.routes.InstRoute中的getPartyInstanceList接口
    sendRequest(
      path = "api/inst/list",
      paramters = Map("page" -> page.toString, "pageSize" -> pageSize.toString),
      method = "post",
      bodyEntity = companyName
    ) map { result =>
      result.parseJson.convertTo[PartyInstanceListEntity]
    }
  }

  def updatePartyInstance(party: String, instanceId: String, companyName: String): Future[String] = {
    //访问com.yimei.cflow.organ.routes.InstRoute中的updatePartyInstance接口
    sendRequest(
      path = "api/inst",
      pathVariables = Array(party, instanceId),
      method = "put",
      bodyEntity = Some(companyName)
    )
  }
}
