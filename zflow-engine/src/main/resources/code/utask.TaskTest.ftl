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


object ${code}UT extends {
  override implicit val coreSystem = ActorSystem("RouteTest", ConfigFactory.parseString(
  """
  |database {
  |  jdbcUrl = "jdbc:mysql://127.0.0.1/cyflow?useUnicode=true&characterEncoding=utf8"
  |  username = "mysql"
  |  password = "mysql"
  |}
  """.stripMargin))
} with Task${code} with DB {
  override val log: LoggingAdapter = Logging(coreSystem, this.getClass)

  def prepare() = {
    val config = coreSystem.settings.config
    val jdbcUrl = config.getString("database.jdbcUrl")
    val username = config.getString("database.username")
    val password = config.getString("database.password")

    val flyway = new FlywayDB(jdbcUrl, username, password)
    flyway.drop()
    flyway.migrate()
  }
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
