package com.yimei.zflow.util.config

import akka.actor.ActorSystem

/**
  * Created by hary on 17/1/6.
  */
trait Core {
  implicit val coreSystem: ActorSystem
}
