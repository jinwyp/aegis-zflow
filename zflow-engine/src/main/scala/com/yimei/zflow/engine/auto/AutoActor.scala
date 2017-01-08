package com.yimei.zflow.engine.auto

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.yimei.zflow.api.GlobalConfig._
import com.yimei.zflow.api.models.auto.CommandAutoTask
import com.yimei.zflow.api.models.flow.{CommandPoints, DataPoint}

import scala.concurrent.Future


/**
  * Created by hary on 16/12/23.
  */
class AutoActor(
                 name: String,
                 modules: Map[String, ActorRef],
                 auto: CommandAutoTask => Future[Map[String, String]]
               )
  extends Actor
    with ActorLogging {

  import scala.concurrent.ExecutionContext
  import ExecutionContext.Implicits.global

  override def receive: Receive = {
    case task: CommandAutoTask =>
      auto(task).map { values =>
        modules(module_flow) ! CommandPoints(
          task.state.flowId,
          values.map { entry =>
            ((entry._1) -> DataPoint(entry._2, None, Some(name), UUID.randomUUID().toString, 10, false))
          }
        )
      }
  }
}
