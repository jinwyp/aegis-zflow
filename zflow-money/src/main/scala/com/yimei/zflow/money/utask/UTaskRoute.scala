package com.yimei.zflow.money.utask

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

/**
  * Created by hary on 17/1/10.
  */
object UTaskRoute extends TaskAssignFriend
  with TaskFillInApplyMessage
  with TaskSwear
  with TaskUploadReceipt
  with TaskWifeApprove
  with TaskWriteFriendEvidence {

  def utaskRoute: Route = getAssignFriend ~ postAssignFriend ~
    getFillInApplyMessage ~ postFillInApplyMessage ~
    getSwear ~ postSwear ~
    getUploadReceipt ~ postUploadReceipt ~
    getWifeApprove ~ postWifeApprove ~
    getWriteFriendEvidence ~ postWriteFriendEvidence

}
