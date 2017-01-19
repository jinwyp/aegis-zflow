package com.yimei.zflow.engine.admin

import java.io.{File, FileInputStream}
import java.nio.file.Paths
import java.nio.file.StandardOpenOption._

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.ContentTypes.`application/octet-stream`
import akka.http.scaladsl.model.headers.{ContentDispositionTypes, `Content-Disposition`}
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.scaladsl.{FileIO, Keep, Sink, Source}
import akka.stream.{IOResult, Materializer}
import akka.util.ByteString
import com.yimei.zflow.api.models.flow.{FlowProtocol, GraphConfig}
import com.yimei.zflow.engine.admin.Models._
import com.yimei.zflow.engine.admin.db.DesignTable
import com.yimei.zflow.engine.admin.db.Entities.DesignEntity
import com.yimei.zflow.engine.graph.GraphLoader
import com.yimei.zflow.util.Archiver
import com.yimei.zflow.util.exception.BusinessException

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by hary on 16/12/28.
  */

trait EditorRoute extends DesignTable with SprayJsonSupport with FlowProtocol {

  import driver.api._

  implicit val editorRouteExecutionContext = coreSystem.dispatcher

  // 1> 用户列出所有流程设计  :   GET /design/graph?page=:page&pageSize=:pageSize
  def listDesign: Route = get {
    (path("graph") & parameter('page.as[Int].?) & parameter('pageSize.as[Int].?)) { (p, ps) =>

      if((p.isDefined && p.get < 0) || (ps.isDefined && ps.get < 0)) throw BusinessException("分页参数有误！")

      val page = if(p.isDefined) p.get else 1
      val pageSize = if(ps.isDefined) ps.get else 10

      val designList = dbrun(designClass.sortBy(d => d.ts_c).map(d => (d.name, d.ts_c)).drop((page - 1) * pageSize).take(pageSize).result)
      val res = for (d <- designList) yield {
        d.map(d => DesignModel(d._1, d._2.get))
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

  // 3> 保存流程设计:      POST /design/graph  + JSON
  def saveDesign: Route = post {
    path("graph") {
      entity(as[SaveDesign]) { design =>
        val designEntity = DesignEntity(None, design.name, design.json, design.meta, None)
        dbrun(designClass.insertOrUpdate(designEntity))
        complete(StatusCodes.OK)
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
      val source = Source.single(entity.json).map(str => ByteString(str))
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
    loadDesign ~ listDesign ~ saveDesign ~ download
  }

}


