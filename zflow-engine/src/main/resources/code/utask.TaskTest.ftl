package ${meta.groupId()}.${meta.artifact()}.utask

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.config.ConfigFactory
import com.yimei.zflow.util.FlywayDB
import com.yimei.zflow.util.DB
import ${meta.groupId()}.${meta.artifact()}.utask.Models._
import com.yimei.zflow.util.HttpResult._
import org.scalatest.{Inside, Matchers, WordSpec}


object ${code}UT extends Task${code} with DB {

  override protected def mkSystem: ActorSystem = ActorSystem("TestSystem", ConfigFactory.parseString(
        """
        |database {
        |  jdbcUrl = "jdbc:mysql://127.0.0.1/cyflow?useUnicode=true&characterEncoding=utf8"
        |  username = "mysql"
        |  password = "mysql"
        |  flyway-schema = "schema"
        |}
        |file.root = "/tmp"
        |akka.http.session.server-secret = "1234567891234567891234567891234567891234567890000012345678901234"
        |
        """.stripMargin))

  def prepare() = FlywayDB(coreSystem).drop.migrate
  override val log: LoggingAdapter = Logging(coreSystem, this.getClass)

}

class Task${code}Test extends WordSpec with Matchers with ScalatestRouteTest {

  import ${code}UT.{get${code}, post${code}}
  import ${code}UT.prepare

  override protected def beforeAll(): Unit = prepare()

  "Task${code}Test" should {

    "getAssignFriend" in {
      Get("/task/${code}") ~> get${code} ~> check {
        responseAs[String] shouldBe "get task/${code}"
      }
    }

    "post${code}" in {
      Post("/task/${code}") ~> post${code} ~> check {
        responseAs[String] shouldBe "post task/${code}"
      }
    }
  }

}
