package com.yimei.zflow.util.organ

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import com.typesafe.config.ConfigFactory
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by hary on 17/1/10.
  */
class OrganServiceTest extends {
  override implicit val coreSystem: ActorSystem = ActorSystem("Test", ConfigFactory.parseString(
    """
      |database {
      |  jdbcUrl = "jdbc:mysql://127.0.0.1/cyflow?useUnicode=true&characterEncoding=utf8"
      |  username = "mysql"
      |  password = "mysql"
      |}
    """.stripMargin))
} with WordSpec with Matchers with OrganService {
  override val log: LoggingAdapter = Logging(coreSystem, getClass)
}
