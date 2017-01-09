package com.yimei.zflow.engine.utask

import akka.util.Timeout
import akka.pattern._
import com.yimei.zflow.api.models.flow.DataPoint
import com.yimei.zflow.api.models.user.{CommandCreateUser, CommandQueryUser, CommandTaskSubmit, State => UserState}

import scala.concurrent.Future

/**
  * Created by hary on 17/1/6.
  */
trait UTaskService {

  val utaskTimeout: Timeout

  import com.yimei.zflow.engine.FlowRegistry.utask

  import scala.concurrent.ExecutionContext.Implicits.global

  // 1> 创建用户
  // 2> 查询用户
  // 3> 用户提交任务
  def utaskCreate(guid: String): Future[UserState] =
    if (utask != null) {
      (utask ? CommandCreateUser(guid)) (utaskTimeout).mapTo[UserState]
    } else {
      Future {
        throw new Exception("utask is not prepared")
      }
    }

  def utaskQuery(guid: String) =
    if (utask != null) {
      (utask ? CommandQueryUser(guid)) (utaskTimeout).mapTo[UserState]
    } else {
      Future {
        throw new Exception("utask is not prepared")
      }
    }

  def utaskSubmit(guid: String, taskId: String, points: Map[String, DataPoint]) =
    if (utask != null) {
      (utask ? CommandTaskSubmit(guid, taskId, points)) (utaskTimeout).mapTo[UserState]
    } else {
      Future {
        throw new Exception("utask is not prepared")
      }
    }
}
