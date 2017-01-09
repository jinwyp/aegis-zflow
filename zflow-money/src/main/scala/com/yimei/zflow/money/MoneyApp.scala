package com.yimei.zflow.money

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.yimei.zflow.money.tasks.TaskRoute
import com.yimei.zflow.util.id.IdGenerator

/**
  * Created by hary on 17/1/9.
  */
object MoneyApp extends App {

  implicit val system = ActorSystem("MoneyFlow")
  implicit val materializer = ActorMaterializer()
  val config = system.settings.config

  // 启动id生成器
  val idGenerator = system.actorOf(IdGenerator.props("IdGenerator"))

  // 启动http服务
  val all = pathPrefix("api") {
    pathPrefix("task") {
      TaskRoute.route
    }
  }
  println(s"http is listening on ${config.getInt("http.port")}")
  Http().bindAndHandle(all, "0.0.0.0", config.getInt("http.port"))
}
