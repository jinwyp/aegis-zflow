package com.yimei.zflow.engine

import akka.actor.ActorRef

/**
  * Created by hary on 17/1/9.
  */
trait ActorService {
  val proxy: ActorRef
}
