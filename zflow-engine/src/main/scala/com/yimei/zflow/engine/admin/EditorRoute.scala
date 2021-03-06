package com.yimei.zflow.engine.admin

import java.io.{File, FileInputStream}
import java.nio.file.Paths
import java.nio.file.StandardOpenOption._
import java.sql.Timestamp

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
import com.yimei.zflow.engine.admin.db.EditorTable
import com.yimei.zflow.engine.admin.db.Entities.EditorEntity
import com.yimei.zflow.engine.graph.GraphLoader
import com.yimei.zflow.util.Archiver
import com.yimei.zflow.util.HttpResult.{PagerInfo, Result}
import com.yimei.zflow.util.exception.BusinessException

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

/**
  * Created by hary on 16/12/28.
  */

trait EditorRoute extends EditorTable with SprayJsonSupport with FlowProtocol {

  import driver.api._

  implicit val editorRouteExecutionContext = coreSystem.dispatcher

  // 1> 用户列出所有流程设计  :   GET /editor/editor?page=:page&pageSize=:pageSize
  def listEditor: Route = get {
    (path("editor") & parameter('page.as[Int].?) & parameter('pageSize.as[Int].?)) { (p, ps) =>

      val page = p.getOrElse(1)
      val pageSize = ps.getOrElse(10)
      require(page > 0 && pageSize > 0, "分页参数不合法！")

      val queryAction = editorClass.sortBy(d => d.ts_c)
          .map(d => (d.name, d.ts_c))
          .drop((page - 1) * pageSize)
          .take(pageSize).result

      val result: Future[Result[Seq[EditorItem]]] = for {
        total <- dbrun(editorClass.size.result)
        dl <- dbrun(queryAction).map(_.map(d => EditorItem(d._1, d._2.get)))
      } yield Result(data = Some(dl), meta = Some(PagerInfo(total = total, count = pageSize, offset = (page - 1) * pageSize + 1, page = page)))

      complete(result)
    }
  }

  // 2> 用户加载流程设计  :  GET /editor/editor/:name  --> JSON
  def loadEditor: Route = get {
    path("editor" / Segment) { name =>

      val design: Future[EditorDetail] = dbrun(editorClass.filter(d => d.name === name).result.head).map { d =>
          EditorDetail(d.name, d.json.getOrElse(""), d.meta.getOrElse(""), d.ts_c.get)
        } recover {
        case NonFatal(e) => throw BusinessException("没有对应的元素！")
      }

      val result = for {
        model <- design
      } yield Result(data = Some(model))

      complete(result)
    }
  }

  // 3> 保存流程设计:      POST /editor/editor  + JSON
  def saveEditor: Route = post {
    path("editor") {
      entity(as[SaveEditor]) { design =>
        val designEntity = EditorEntity(None, design.name, design.json, design.meta, None)
        dbrun(editorClass.insertOrUpdate(designEntity))
        val result = Result(data = Some("ok"))

        complete(result)
      }
    }
  }

  // 4> 下载模板项目:      GET /editor/download/:name
  def download: Route = get {
    (extractExecutionContext & extractMaterializer & path("download" / Segment)) { (ec, mat, name) =>

      val entity: Future[EditorEntity] = dbrun(editorClass.filter(d => d.name === name).result.head)

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

  private def genTar(entityFuture: Future[EditorEntity])(implicit ec: ExecutionContext, mat: Materializer): Future[(Source[ByteString, Future[IOResult]], String)] = {
    // 生成文件到/tmp/flow.json, 并产生GraphConfig
    def getConfig(entity: EditorEntity): Future[GraphConfig] = {
      val sink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(Paths.get("/tmp/flow.json"), Set(CREATE, WRITE))
      val source = Source.single(entity.json.get).map(str => ByteString(str))
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
  def editorRoute = pathPrefix("editor") {
    loadEditor ~ listEditor ~ saveEditor ~ download
  }

}


