package com.yimei.zflow.util.config

import akka.stream.ActorMaterializer
import akka.util.Timeout
import scala.concurrent.duration._

/**
  * Created by hary on 17/1/9.
  */
trait CoreConfig extends Core {
  implicit val coreTimeout = Timeout(5 seconds)
  implicit val coreExecutor = coreSystem.dispatcher
  implicit val coreMaterializer = ActorMaterializer()
  val coreConfig = coreSystem.settings.config

}
