package com.yimei.zflow.api.http.models

import com.yimei.zflow.api.models.flow.DataPoint
import com.yimei.zflow.api.models.group.{GroupProtocol, State}
import com.yimei.zflow.api.models.user.UserProtocol
import spray.json.DefaultJsonProtocol

/**
  * Created by xl on 16/12/23.
  */
object TaskModel {

  case class UserSubmitEntity(flowId:String,taskName:String,points:Map[String,DataPoint])

  case class GroupTaskResult(tasks:Seq[State],total:Int)

  case class UserSubmitMap(memo:Option[String],value: String)


  trait TaskProtocol extends DefaultJsonProtocol with UserProtocol with GroupProtocol{

    //implicit val userTaskEntityFormat = jsonFormat3(DataPoint)
    implicit val userSubmintEntity = jsonFormat3(UserSubmitEntity)
    implicit val groupTaskFromat = jsonFormat2(GroupTaskResult)
    implicit val userSubmitMapFormat = jsonFormat2(UserSubmitMap)

  }
}
