package com.yimei.zflow.util.asset.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, Multipart}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.config.ConfigFactory
import com.yimei.zflow.util.FlywayDB
import com.yimei.zflow.util.asset.routes.Models._
import com.yimei.zflow.util.organ.OrganSession
import org.scalatest.{Inside, Matchers, WordSpec}


object AssetRouteUT extends AssetRoute {

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

  val fileField = "file"

  def prepare() = FlywayDB(coreSystem).drop.migrate

//  {
//    val config = coreSystem.settings.config
//    val jdbcUrl = config.getString("database.jdbcUrl")
//    val username = config.getString("database.username")
//    val password = config.getString("database.password")
//    val flyway = new FlywayDB(jdbcUrl, username, password);
//    flyway.drop()
//    flyway.migrate()
//  }

  import akka.http.scaladsl.server.Directives._

  def loginRoute: Route = path("login") {
    organSetSession(OrganSession("hary", "uid", "party", "instanceId", "company")) { ctx =>
      ctx.complete("ok")
    }
  }
}

/**
  * Created by hary on 17/1/10.
  */
class AssetRouteTest extends WordSpec
  with Matchers
  with Inside
  with ScalatestRouteTest
  with SprayJsonSupport {

  import AssetRouteUT.{assetRoute, loginRoute, prepare}

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    prepare
  }

  "AssetRouteTest" should {

    "login && upload && download" in {
      Post("/login") ~> loginRoute ~> check {
        val cookie = header("Set-Cookie").get
        // responseAs[String] shouldBe "ok"
        // println("cookie is " + cookie.toString)

        val xml = "<int>42</int>"
        // 上传文件
        val content = Multipart.FormData(
          Multipart.FormData.BodyPart.Strict(
            "file",
            HttpEntity(ContentTypes.`text/xml(UTF-8)`, xml),
            Map("filename" -> "age.xml") // 必须是filename, disposition parameters
          )
        )

        Post("/upload", content) ~> addHeader(cookie) ~> assetRoute ~> check {
          val result = responseAs[UploadResult]
          val cookieOpt = header("Set-Cookie")
          println("get result: " + result + " cookieOpt: " + cookieOpt)
          Get("/download/" + result.id) ~> addHeader(cookie) ~> assetRoute ~> check {
            responseAs[String] shouldEqual xml
          }
        }
      }
    }
  }

}

