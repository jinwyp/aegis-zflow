package com.yimei.zflow.engine.utask

import akka.pattern._
import akka.util.Timeout
import com.yimei.zflow.api.models.flow.DataPoint
import com.yimei.zflow.api.models.utask.{CommandCreateUser, CommandQueryUser, CommandTaskSubmit, State => UserState}
import com.yimei.zflow.engine.db.Entities.FlowTaskEntity
import com.yimei.zflow.engine.db.FlowTaskTable
import com.yimei.zflow.engine.utask.Models._
import com.yimei.zflow.util.config.Core

import scala.concurrent.Future

/**
  * Created by hary on 17/1/6.
  */
trait UTaskService extends FlowTaskTable with Core{

  val utaskTimeout: Timeout
  import driver.api._
  import coreSystem.dispatcher

  import com.yimei.zflow.engine.FlowRegistry.utask

  // 1> 创建用户
  // 2> 查询用户
  // 3> 用户提交任务
//  def utaskCreate(guid: String): Future[UserState] =
//    if (utask != null) {
//      (utask ? CommandCreateUser(guid)) (utaskTimeout).mapTo[UserState]
//    } else {
//      Future.failed(new Exception("utask is not prepared"))
//    }

  private def toFlowTaskEntry(f:FlowTaskEntity) = FlowTaskEntry(f.flow_id,f.flow_type,f.task_id,f.task_name,f.task_submit,f.guid,f.ts_c)

  /**
    * 查询task list
    * @param guid
    * @param flowType
    * @param flowId
    * @param page
    * @param pageSize
    * @return
    */
  def taskList(guid:Option[String], flowType:Option[String], flowId:Option[String], page:Option[Int], pageSize:Option[Int]): Future[ListTaskResponse] = {
    val (pg, pz) = (page, pageSize) match {
      case (Some(p), Some(s)) if p >=1 && s >= 1 => (p, s)
      case _ => (1, 10)
    }

    val query = flowTask.filter(f =>
      List(
        guid.map(f.guid === _),
        flowType.map(f.flow_type === _),
        flowId.map(f.flow_id === _)
      ).collect({ case Some(a) => a })
        .reduceLeftOption(_ && _)
        .getOrElse(true: Rep[Boolean]))


    val tasks: Future[Seq[FlowTaskEntity]] = dbrun(query.drop((pg-1) * pz).take(pz).result)
    val total = dbrun(query.length.result)

    for {
      ts <- tasks
      t  <- total
    } yield ListTaskResponse(ts.map(toFlowTaskEntry(_)),t)



  }

  def utaskQuery(guid: String) =
    if (utask != null) {
      (utask ? CommandQueryUser(guid)) (utaskTimeout).mapTo[UserState]
    } else {
      Future.failed(new Exception("utask is not prepared"))
    }

  def utaskSubmit(guid: String, taskId: String, points: Map[String, DataPoint]) =
    if (utask != null) {
      (utask ? CommandTaskSubmit(guid, taskId, points)) (utaskTimeout).mapTo[UserState]
    } else {
      Future.failed(new Exception("utask is not prepared"))
    }
}
