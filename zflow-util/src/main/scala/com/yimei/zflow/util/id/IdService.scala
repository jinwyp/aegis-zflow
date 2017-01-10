package com.yimei.zflow.util.id

import akka.actor.ActorRef
import akka.pattern._
import akka.util.Timeout
import com.yimei.zflow.util.id.models.{CommandGetId, CommandQueryId, Id, State}

/**
  * Created by hary on 17/1/6.
  */
trait IdService {

  val id: ActorRef;

  def idServiceTimeout: Timeout

  def idGet(key: String, buffer: Int = 1) = (id ? CommandGetId(key, buffer))(idServiceTimeout).mapTo[Id]

  def idState = (id ? CommandQueryId)(idServiceTimeout).mapTo[State]
}
