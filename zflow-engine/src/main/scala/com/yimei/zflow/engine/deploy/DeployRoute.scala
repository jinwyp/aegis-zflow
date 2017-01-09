package com.yimei.zflow.engine.deploy

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import com.yimei.zflow.util.DB

import scala.concurrent.Future

/**
  * Created by hary on 16/12/28.
  */
trait DeployRoute extends DB {


  // POST /deploy/:flowType  + fileupload
  private def deployUpload = extractRequestContext { ctx =>
    implicit val materializer = ctx.materializer
    implicit val ec = ctx.executionContext

    // 实际作的应该是保存数据库, loadall
    fileUpload("csv") {
      case (metadata, byteSource) =>
        // sum the numbers as they arrive so that we can
        // accept any size of file
        //          byteSource.via(Framing.delimiter(ByteString("\n"), 1024))
        //            .mapConcat(_.utf8String.split(",").toVector)
        //            .map(_.toInt)
        //            .runFold(0) { (acc, n) => acc + n }
        val f: Future[Int] = null
        onComplete(f) { k =>
          complete("hello")
        }
    }
  }

  def deployRoute: Route = deployUpload
}


