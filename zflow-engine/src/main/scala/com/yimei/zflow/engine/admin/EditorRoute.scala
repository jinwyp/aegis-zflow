package com.yimei.zflow.engine.admin

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.ContentTypes.`application/octet-stream`
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.yimei.zflow.engine.db.DesignTable
import com.yimei.zflow.engine.db.Entities.DesignEntity
import com.yimei.zflow.engine.admin.Models._

import scala.concurrent.Future

/**
  * Created by hary on 16/12/28.
  */

trait EditorRoute extends DesignTable with SprayJsonSupport {

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
      complete(
        HttpResponse(
          status = StatusCodes.OK,
          entity = HttpEntity(`application/octet-stream`, Source.fromFuture(genCode(id)))
        )
      )
    }
  }

  /**
    * freemarker template engine to gen code
    * 依照zflow-money的项目结构, 依据flow.json文件生成项目模板到/tmp/zflow-xxxx
    * 同时将zflow-xxx打包为zflow-xxx.tar.gz
    *
    * 最终产生一个生成个文件的Future[ByteString]
    */
  private def genCode(id: Long): Future[ByteString] = {
    Future.failed(new Exception("failed to gencode"))
  }

  // 总路由
  def editorRoute = pathPrefix("design") {
    listDesign ~ loadDesign ~ saveDesign ~ download
  }

}


