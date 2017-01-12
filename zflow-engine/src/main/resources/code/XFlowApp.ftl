package ${meta.groupId()}.${meta.artifact()}

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.RouteResult
import akka.http.scaladsl.server.directives.LogEntry
import akka.stream.ActorMaterializer
import ${meta.groupId()}.${meta.artifact()}.utask.UTaskRoute
import com.yimei.zflow.util.id.IdGenerator

object ${meta.entry()}App extends App {

  implicit val system = ActorSystem("${meta.entry()}Flow")
  implicit val materializer = ActorMaterializer()
  val config = system.settings.config

  // 启动id生成器
  val idGenerator = system.actorOf(IdGenerator.props("IdGenerator"))

  def extractLogInfo(req: HttpRequest): RouteResult => Option[LogEntry] = {
    case RouteResult.Complete(res) =>
      Some(LogEntry(req.method.name +  " " + req.uri.path + " : " + res.status, Logging.InfoLevel))
    case _ => None // no log entries for rejections
  }

  // 启动http服务
  val all = logRequestResult(extractLogInfo _) {
    pathPrefix("api") {
      UTaskRoute.utaskRoute  // 可以添加其他非
    }
  }
  Http().bindAndHandle(all, "0.0.0.0", config.getInt("http.port"))
}

