package com.yimei.zflow.engine.editor

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import com.yimei.zflow.engine.db.DesignTable
import com.yimei.zflow.engine.db.Entities.DesignEntity
import com.yimei.zflow.engine.editor.Models._
/**
  * Created by hary on 16/12/28.
  */

trait EditorRoute extends DesignTable with SprayJsonSupport {

  import driver.api._

  implicit val editorRouteExecutionContext = coreSystem.dispatcher

  // 1> 用户列出所有流程设计  :   GET /design/graph
  def listDesign: Route =  get {
    path("design" / "graph") {
        val designList = dbrun(designClass.sortBy(d => d.ts_c).map(d => (d.id, d.name, d.ts_c)).result)
        val res = for (d <- designList) yield { d.map(d => DesignList(d._1.get, d._2, d._3.get))}
        complete(res)
    }
  }

  // 2> 用户加载流程设计  :  GET /design/graph/:id  --> JSON
  def loadDesign: Route = get {
    path("design" / "graph" / LongNumber ) { id =>
      val design = dbrun(designClass.filter(d => d.id === id).result.head)
      complete(design.map(d => DesignDetail(d.id.get, d.name, d.json, d.meta, d.ts_c.get)))
    }
  }

  // 3> 保存流程设计:      POST /design/graph?id=:id  + JSON
  def saveDesign: Route =  post {
    path("design" / "graph" ) {
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
    path("design" / "download" / Segment ) { id =>
      complete(s"download $id")
    }
  }

  // 总路由
  def editorRoute = listDesign ~ loadDesign ~ saveDesign ~ download

}


