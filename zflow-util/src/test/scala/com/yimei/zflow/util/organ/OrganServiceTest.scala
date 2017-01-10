package com.yimei.zflow.util.organ

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by hary on 17/1/10.
  */
class OrganServiceTest extends WordSpec with Matchers with OrganService {
  override def log: LoggingAdapter = null

  override implicit val coreSystem: ActorSystem = null


}
