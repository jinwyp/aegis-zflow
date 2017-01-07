package com.yimei.zflow.util.id

import akka.actor.ActorRef
import akka.pattern._
import akka.util.Timeout
import com.yimei.zflow.util.id.models.{CommandGetId, CommandQueryId, Id, State}

/**
  * Created by hary on 17/1/6.
  */
trait IdService {

  def proxy: ActorRef;

  def idServiceTimeout: Timeout

  def idGet(key: String, buffer: Int = 1) = (proxy ? CommandGetId(key, buffer))(idServiceTimeout).mapTo[Id]

  def idState = (proxy ? CommandQueryId)(idServiceTimeout).mapTo[State]
}
