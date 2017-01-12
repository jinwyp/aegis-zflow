package #{groupId}.#{artifact}.utask

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import #{groupId}.#{artifact}.utask.Models._

trait Task#{utask} {

  def get#{utask}: Route = get {
    path("task" / "#{utask}" ) {
      complete("get task/#{utask}")
    }
  }

  def post#{utask}: Route = post {
    path("task" / "#{utask}") {
      complete("post task/#{utask}")
    }
  }

}


