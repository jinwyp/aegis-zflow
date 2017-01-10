package com.yimei.zflow.util.id

import akka.actor.ActorRef
import akka.util.Timeout
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Created by hary on 17/1/10.
  */
class IdBufferableTest extends WordSpec with Matchers {

  info("IdBufferable testing now...")


  "IdBufferableTest" should {

    "nextId" in {
      "hello" shouldEqual "hello"
    }

  }
}
