package com.yimei.zflow.api.http.models

import com.yimei.zflow.api.models.flow.DataPoint
import com.yimei.zflow.api.models.user.UserProtocol
import com.yimei.zflow.api.models.database.FlowDBModel._
import spray.json.DefaultJsonProtocol

/**
  * Created by xl on 16/12/23.
  */
object AdminModel {

  case class HijackEntity(updatePoints: Map[String, DataPoint], decision: Option[String], trigger: Boolean)

  case class AllTasks(finishedTask: Seq[FlowInstanceEntity], processTask: Seq[FlowInstanceEntity], total: Int)

  case class FlowQuery(flowId: Option[String], flowType: Option[String], userType: Option[String], userId: Option[String], status: Option[Int], page: Option[Int], pageSize: Option[Int])

  case class FlowQueryResponse(flows: Seq[FlowInstanceEntity], total: Int)

  case class FlowQueryByUserEntity(flowType: Option[String], status: Option[Int], limit: Option[Int], offset: Option[Int])

  trait AdminProtocol extends DefaultJsonProtocol with UserProtocol {
    implicit val hijackEntityFormat = jsonFormat3(HijackEntity)
    implicit val allTaskFormat = jsonFormat3(AllTasks)
    implicit val flowQuery = jsonFormat7(FlowQuery)
    implicit val flowQueryResponseFormat = jsonFormat2(FlowQueryResponse)
    implicit val flowQueryByUserEntityFormat = jsonFormat4(FlowQueryByUserEntity)
  }
}
