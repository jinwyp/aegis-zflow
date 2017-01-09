package com.yimei.zflow.money

import com.wix.accord.Validator
import com.wix.accord.dsl._
import spray.json.DefaultJsonProtocol

/**
  * Created by hary on 17/1/9.
  */
object MoneyPoints extends DefaultJsonProtocol {

  case class PointWife(name: String)
  implicit val pointWifeFormat = jsonFormat1(PointWife)
  case class PointSuccessRate()
  case class PointReason()
  case class PointAmount()
  case class PointLoanReceipt()
  case class PointFriend()
  case class PointFriendEvidence()
  case class PointRepaymentTime()
  case class PointUnequalTreaty()
  case class PointApprove()

  // validations
  object Validate {
    implicit val PointWifeValidate: Validator[PointWife] = validator[PointWife] { wife =>
      wife.name.length as "长度"  is between(1,200)
    }
  }
}

