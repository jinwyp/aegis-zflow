package com.yimei.zflow.api.http.client
import com.yimei.zflow.api.http.models.ResultModel._
import com.yimei.zflow.api.models.database.FlowDBModel.FlowInstanceEntity
import com.yimei.zflow.api.models.user.UserProtocol
import HttpUtil._
import com.yimei.zflow.engine.util.HttpUtil
import spray.json._

import scala.concurrent.Future

/**
  * Created by hary on 16/12/23.
  */
trait AdminClient extends ResultProtocol with UserProtocol  {

  def createFlow(party:String,instance_id:String,user_id:String,flowType:String,initdata:Map[String,String]): Future[FlowInstanceEntity] = {
    sendRequest(path = "api/flow/user",paramters = Map("flowType"->flowType),pathVariables = Array(party,instance_id,user_id),
      method = "post",bodyEntity = Some(initdata.toJson.toString)) map { result =>
        result.parseJson.convertTo[FlowInstanceEntity]

    }
  }

}
