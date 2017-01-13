package com.yimei.zflow.engine.admin

import java.io.{File, FileInputStream}
import java.nio.file.Paths

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.ContentTypes.`application/octet-stream`
import akka.http.scaladsl.model.headers.{ContentDispositionTypes, `Content-Disposition`}
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Source}
import akka.util.ByteString
import com.yimei.zflow.api.models.graph.{GraphConfig, GraphConfigProtocol}
import com.yimei.zflow.engine.admin.Models._
import com.yimei.zflow.engine.admin.db.DesignTable
import com.yimei.zflow.engine.admin.db.Entities.DesignEntity
import com.yimei.zflow.util.Archiver

import scala.concurrent.Future
import scala.io.BufferedSource

/**
  * Created by hary on 16/12/28.
  */

trait EditorRoute extends DesignTable with SprayJsonSupport with GraphConfigProtocol {

  import driver.api._

  implicit val editorRouteExecutionContext = coreSystem.dispatcher

  // 1> 用户列出所有流程设计  :   GET /design/graph
  def listDesign: Route = get {
    path("graph") {
      val designList = dbrun(designClass.sortBy(d => d.ts_c).map(d => (d.id, d.name, d.ts_c)).result)
      val res = for (d <- designList) yield {
        d.map(d => DesignList(d._1.get, d._2, d._3.get))
      }
      complete(res)
    }
  }

  // 2> 用户加载流程设计  :  GET /design/graph/:id  --> JSON
  def loadDesign: Route = get {
    path("graph" / LongNumber) { id =>
      val design = dbrun(designClass.filter(d => d.id === id).result.head)
      complete(design.map(d => DesignDetail(d.id.get, d.name, d.json, d.meta, d.ts_c.get)))
    }
  }

  // 3> 保存流程设计:      POST /design/graph?id=:id  + JSON
  def saveDesign: Route = post {
    path("graph") {
      parameter("id".as[Long].?) { id =>
        entity(as[SaveDesign]) { design =>
          val designEntity = DesignEntity(id, design.name, design.json, design.meta, None)
          designClass.insertOrUpdate(designEntity)
          complete(StatusCodes.OK)
        }
      }
    }
  }

  // 4> 下载模板项目:      GET /design/download/:id
  def download: Route = get {
    path("download" / LongNumber) { id =>
      onSuccess(genTar(id)) {
        case (source, name) =>
          complete(
            HttpResponse(
              status = StatusCodes.OK,
              entity = HttpEntity(`application/octet-stream`, source),
              headers = List(`Content-Disposition`(ContentDispositionTypes.attachment, Map("filename" -> name)))
            )
          )
      }
    }
  }

  /**
    * freemarker template engine to gen code
    * 依照zflow-money的项目结构, 依据flow.json文件生成项目模板到/tmp/zflow-xxxx
    * 同时将zflow-xxx打包为zflow-xxx.tar.gz
    *
    * 最终产生一个生成个文件的Future[ByteString]
    */
  private def genTar(id: Long): Future[(Source[ByteString, Future[IOResult]], String)] = {

    // todo 测试!!!
    // 用id从design表中将json字段读取出来, 目前是直接从money.json文件读取
    import spray.json._
    val config =
      scala.io.Source
        .fromInputStream(this.getClass.getClassLoader.getResourceAsStream("flow.json"))
        .mkString
        .parseJson.convertTo[GraphConfig]

    val fconfig: Future[GraphConfig] = Future { config }

    for {
      config <- fconfig
      result <- CodeEngine.genAll(config)(coreSystem).map { entry =>
        val root = entry._1
        val name = entry._2
        val srcPath = root + File.separator + name
        Archiver.archive(srcPath, srcPath + ".tar")
        (FileIO.fromPath(Paths.get(root + File.separator + name + ".tar")), name + ".tar")
      }
    } yield result
  }


  // 总路由
  def editorRoute = pathPrefix("design") {
    listDesign ~ loadDesign ~ saveDesign ~ download
  }

}


