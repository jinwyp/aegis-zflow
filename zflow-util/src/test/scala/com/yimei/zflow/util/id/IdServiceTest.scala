package com.yimei.zflow.util.id

import akka.actor.ActorRef
import akka.util.Timeout
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by hary on 17/1/10.
  */
class IdServiceTest extends WordSpec with Matchers with IdService {

  override val id: ActorRef = null
  override def idServiceTimeout: Timeout = null

  "IdServiceTest" should {
    "testIdState" in {
      1 shouldBe 1
    }

    "testIdGet" in {
      1 shouldBe 1
    }
  }
}
