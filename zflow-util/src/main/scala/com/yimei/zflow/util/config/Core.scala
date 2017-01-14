package com.yimei.zflow.util.config

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.stream.ActorMaterializer

/**
  * Created by hary on 17/1/9.
  */
trait Core {
  implicit val coreSystem: ActorSystem
  implicit val coreMaterializer = ActorMaterializer()
  val coreConfig = coreSystem.settings.config
  val log = Logging(coreSystem, this.getClass)
}
