package ${meta.groupId()}.${meta.artifact()}.utask

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import ${meta.groupId()}.${meta.artifact()}.utask.Models._

trait Task${code} {

  def get${code}: Route = get {
    path("task" / "${code}" ) {
      complete("get task/${code}")
    }
  }

  def post${code}: Route = post {
    path("task" / "${code}") {
      complete("post task/${code}")
    }
  }

}


