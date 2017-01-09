package com.yimei.zflow.util.config

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

/**
  * Created by hary on 17/1/9.
  */
trait Core {
  implicit val coreSystem: ActorSystem
  implicit val coreMaterializer = ActorMaterializer()
}
