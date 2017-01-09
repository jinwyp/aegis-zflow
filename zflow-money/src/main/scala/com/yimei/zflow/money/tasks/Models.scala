package com.yimei.zflow.money.tasks

import spray.json.DefaultJsonProtocol

/**
  * Created by hary on 17/1/9.
  */
object Models extends DefaultJsonProtocol {

  // task approve
  case class ApproveView(status: String)
  implicit val ApproveViewFormat = jsonFormat1(ApproveView)

  case class ApproveSubmit(k1: Int, k2: Int)
  implicit val ApproveSubmitFormat = jsonFormat2(ApproveSubmit)


  // task xxxx
}

