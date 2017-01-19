package com.yimei.zflow.engine.admin

import java.io.{BufferedWriter, File, FileInputStream, FileWriter}
import java.nio.charset.StandardCharsets
import java.nio.file.StandardOpenOption._
import java.nio.file.{Files, Path, Paths, StandardOpenOption}

import akka.NotUsed
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.ContentTypes.`application/octet-stream`
import akka.http.scaladsl.model.StatusCodes.Success
import akka.http.scaladsl.model.headers.{ContentDispositionTypes, `Content-Disposition`}
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.{ActorMaterializer, IOResult, Materializer}
import akka.stream.scaladsl.{FileIO, Keep, RunnableGraph, Sink, Source}
import akka.util.ByteString
import com.yimei.zflow.api.models.flow.{FlowProtocol, GraphConfig}
import com.yimei.zflow.engine.admin.Models._
import com.yimei.zflow.engine.admin.db.DesignTable
import com.yimei.zflow.engine.admin.db.Entities.DesignEntity
import com.yimei.zflow.engine.graph.GraphLoader
import com.yimei.zflow.util.Archiver
import com.yimei.zflow.util.exception.BusinessException
import org.apache.commons.io.FileUtils

import scala.concurrent.{ExecutionContext, Future}
import scala.io.BufferedSource
import scala.sys.process.Process

/**
  * Created by hary on 16/12/28.
  */

trait EditorRoute extends DesignTable with SprayJsonSupport with FlowProtocol {

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

  // 2> 用户加载流程设计  :  GET /design/graph/:name  --> JSON
  def loadDesign: Route = get {
    path("graph" / Segment) { name =>
      val design: Future[DesignEntity] = dbrun(designClass.filter(d => d.name === name).result.head)
      complete(design map { entity => DesignDetail(entity.id.get, entity.name, entity.json, entity.meta, entity.ts_c.get) })
    }
  }

  // 3> 保存流程设计:      POST /design/graph?id=:id  + JSON
  def saveDesign: Route = post {
    path("graph") {
      parameter("id".as[Long].?) { id =>
        entity(as[SaveDesign]) { design =>
          if (design.id.isDefined && design.id != id) throw BusinessException("body里面的id与路径上的id不一致！")
          val designEntity = DesignEntity(id, design.name, design.json, design.meta, None)
          dbrun(designClass.insertOrUpdate(designEntity))
          complete(StatusCodes.OK)
        }
      }
    }
  }

  // 4> 下载模板项目:      GET /design/download/:name
  def download: Route = get {
    (extractExecutionContext & extractMaterializer & path("download" / Segment)) { (ec, mat, name) =>

      val entity: Future[DesignEntity] = dbrun(designClass.filter(d => d.name === name).result.head)

      onSuccess(genTar(entity)(ec, mat)) {
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

  private def genTar(entityFuture: Future[DesignEntity])(implicit ec: ExecutionContext, mat: Materializer): Future[(Source[ByteString, Future[IOResult]], String)] = {
    // 生成文件到/tmp/flow.json, 并产生GraphConfig
    def getConfig(entity: DesignEntity): Future[GraphConfig] = {
      val sink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(Paths.get("/tmp/flow.json"), Set(CREATE, WRITE))
      val source  = Source.single(entity.json).map(str => ByteString(str))
      source.toMat(sink)(Keep.right).run()
    }.map { result: IOResult =>
      GraphLoader.loadConfig(new FileInputStream("/tmp/flow.json"))
    }

    for {
      entity <- entityFuture
      config <- getConfig(entity)
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
    loadDesign ~ listDesign ~ saveDesign ~ download ~ genFile
  }

}


