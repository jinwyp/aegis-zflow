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

<#--case class Vertex(description: String, next: Option[Arrow])-->


<#list code.vertices()?keys as v>
  // ${code.vertices()[v].description()}
  // ${code.vertices()[v].next().toString()}
<#if code.vertices()[v].next().toString() == "None">
  def ${v}(state: State) = ???
<#else>
  def ${v}(state: State) = Seq(Arrow(vertex_${code.vertices()[v].next().get().end()}, Some(edge_${code.vertices()[v].next().get().edge().get()})))
</#if>

</#list>

<#list code.auto()?keys as auto>
  // ${code.auto()[auto].description()}
  def ${auto}(cmd: CommandAutoTask): Future[Map[String, String]] = ???

</#list>

  // 这里可以加上其他的route
  def moduleRoute(): Route = UTaskRoute.utaskRoute
}


