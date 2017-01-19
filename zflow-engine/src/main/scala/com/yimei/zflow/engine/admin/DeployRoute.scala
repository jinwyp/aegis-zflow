package com.yimei.zflow.engine.admin

import java.io.{File, FileInputStream, FileOutputStream, InputStream}
import java.sql.Blob
import javax.sql.rowset.serial.SerialBlob

import akka.http.scaladsl.model.RequestEntity
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.scaladsl.{Framing, Keep, RunnableGraph, Sink, Source, StreamConverters}
import akka.util.ByteString
import com.yimei.zflow.engine.admin.db.DeployTable
import com.yimei.zflow.engine.admin.db.Entities.DeployEntity
import com.yimei.zflow.util.DB
import com.yimei.zflow.util.exception.DatabaseException
import org.apache.commons.io.{FileUtils, IOUtils}

import scala.concurrent.Future

/**
  * Created by hary on 16/12/28.
  */
trait DeployRoute extends DB with DeployTable {

  import driver.api._
  //implicit val assetRouteExecutionContext = coreSystem.dispatcher

  // POST /deploy/:flowType  + fileupload
  private def deployUpload = (post & withSizeLimit(100 * 1024 * 1024)) {
    (path("deploy" / Segment) & extractRequestContext) { (flowType, ctx) =>
      implicit val materializer = ctx.materializer
      implicit val ec = ctx.executionContext
      // 实际作的应该是保存数据库, loadall
      fileUpload("file") {
        case (metadata, byteSource: Source[ByteString, Any]) =>
          // sum the numbers as they arrive so that we can
          // accept any size of file
          val sink: Sink[ByteString, InputStream] = StreamConverters.asInputStream()
          val inputStream: InputStream = byteSource.toMat(sink)(Keep.right).run()
          val bytes: Array[Byte] = IOUtils.toByteArray(inputStream)
          val result: Future[Int] = dbrun(deployClass += DeployEntity(None, flowType, new SerialBlob(bytes), true, None)) recover {
            case e =>
              log.error("{}", e)
              throw DatabaseException(s"deploy数据库插出错，${e.getMessage}")
          }
          onSuccess(result) { i =>
            log.info("插入条数{}", i)
            complete(s"sum : ${bytes.length}, flowType : $flowType")
          }
      }
    }
  }

  private def deploy = get {
    (path("deploy" / Segment) & extractRequestContext) { (flowType, ctx) =>
      implicit val materializer = ctx.materializer
      implicit val ec = ctx.executionContext
      val blob: Future[Blob] = dbrun(deployClass.filter(_.flow_type === flowType).map(_.jar).result.head)

      def delpoyFile(blob: Blob): String = {
        val file = new File("./flows/" + flowType + "/" + flowType + ".jar")
        FileUtils.forceMkdir(file.getParentFile)
        val outputStream = new FileOutputStream(file)

        IOUtils.copy(blob.getBinaryStream, outputStream)
        outputStream.close()
        "success"
      }

      complete(for {
        b <- blob
      } yield delpoyFile(b))
    }
  }

  def deployRoute: Route = deployUpload ~ deploy
}


