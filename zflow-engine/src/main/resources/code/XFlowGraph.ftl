package ${meta.groupId()}.${meta.artifact()}

import akka.http.scaladsl.server.Route
import com.yimei.zflow.api.models.auto.CommandAutoTask
import com.yimei.zflow.api.models.flow.{Arrow, _}
import ${meta.groupId()}.${meta.artifact()}.config.${meta.entry()}Config._
import ${meta.groupId()}.${meta.artifact()}.utask.UTaskRoute

import scala.concurrent.Future

object ${meta.entry()}Graph {

  import scala.concurrent.ExecutionContext.Implicits.global

  val ArrowFail = Arrow("fail", None)
  val ArrowSuccess = Arrow("success", None)

<#list code.vertices()?keys as vertex>
  def ${vertex}(state: State) = ???
</#list>

<#list code.auto()?keys as auto>
  def ${auto}(cmd: CommandAutoTask): Future[Map[String, String]] = ???
</#list>

  // 这里可以加上其他的route
  def moduleRoute(): Route = UTaskRoute.utaskRoute
}

<#--{ Seq(Arrow(vertex_${end}, Some(edge_${edge}))) }-->
