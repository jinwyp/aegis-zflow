package com.yimei.zflow.engine.utask

import java.sql.SQLIntegrityConstraintViolationException

import akka.pattern._
import akka.util.Timeout
import com.yimei.zflow.api.models.utask.{CommandQueryUser, CommandTaskSubmit, State => UserState}
import com.yimei.zflow.engine.db.Entities.FlowTaskEntity
import com.yimei.zflow.engine.db.FlowTaskTable
import com.yimei.zflow.engine.utask.Models._
import com.yimei.zflow.util.config.Core
import com.yimei.zflow.util.exception.DatabaseException
import spray.json._

import scala.concurrent.Future

/**
  * Created by hary on 17/1/6.
  */
trait UTaskService extends FlowTaskTable with Core {

  val utaskTimeout: Timeout

  import com.yimei.zflow.engine.FlowRegistry.utask
  import coreSystem.dispatcher
  import driver.api._

  // 1> 创建用户
  // 2> 查询用户
  // 3> 用户提交任务
  //  def utaskCreate(guid: String): Future[UserState] =
  //    if (utask != null) {
  //      (utask ? CommandCreateUser(guid)) (utaskTimeout).mapTo[UserState]
  //    } else {
  //      Future.failed(new Exception("utask is not prepared"))
  //    }

  private def toFlowTaskEntry(f: FlowTaskEntity) = FlowTaskEntry(f.flow_id, f.flow_type, f.task_id, f.task_name, f.task_submit, f.guid, f.ts_c)

  /**
    * 查询task list
    *
    * @param guid
    * @param flowType
    * @param flowId
    * @param page
    * @param pageSize
    * @return
    */
  def taskList(guid: Option[String], flowType: Option[String], flowId: Option[String], page: Option[Int], pageSize: Option[Int]): Future[ListTaskResponse] = {
    val (pg, pz) = (page, pageSize) match {
      case (Some(p), Some(s)) if p >= 1 && s >= 1 => (p, s)
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


    val tasks: Future[Seq[FlowTaskEntity]] = dbrun(query.drop((pg - 1) * pz).take(pz).result)
    val total = dbrun(query.length.result)

    for {
      ts <- tasks
      t <- total
    } yield ListTaskResponse(ts.map(toFlowTaskEntry(_)), t)


  }

  /**
    * 查询指定用户任务
    *
    * @param guid
    * @return
    */
  def utaskQuery(guid: String) =
    if (utask != null) {
      (utask ? CommandQueryUser(guid)) (utaskTimeout).mapTo[UserState]
    } else {
      Future.failed(new Exception("utask is not prepared"))
    }


  /**
    * 提交指定任务
    * @param guid
    * @param taskId
    * @param flowType
    * @param entity
    * @return
    */
  def utaskSubmit(guid: String, taskId: String, flowType: String, entity: UserSubmitEntity): Future[UserState] =
    if (utask != null) {
      def insertTask(s: UserState): Future[FlowTaskEntity] = {
        dbrun(flowTask returning flowTask.map(_.id) into ((fl, id) => fl.copy(id = id)) +=
          FlowTaskEntity(None, entity.flowId, flowType, taskId, entity.taskName, entity.points.toJson.toString, guid, None)
        ) recover {
          case a: SQLIntegrityConstraintViolationException => throw DatabaseException("当前任务已被提交")
        }
      }
      for {
        s <- (utask ? CommandTaskSubmit(guid, taskId, entity.points)) (utaskTimeout).mapTo[UserState]
        ft <- insertTask(s)
      } yield s

    } else {
      Future.failed(new Exception("utask is not prepared"))
    }
}
