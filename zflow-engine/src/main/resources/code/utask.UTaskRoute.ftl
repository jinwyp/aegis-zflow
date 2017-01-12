package ${meta.groupId()}.${meta.artifact()}.utask

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

<#assign first = code[0]>
object UTaskRoute extends Task${first}
<#list code as task>
<#if task != first>
  with Task${task}
</#if>
</#list>
{

  def utaskRoute: Route =
<#list code as utask>
    get${utask} ~ post${utask} ~
</#list>
    get {
      path("tasks") {
         complete("ok")
      }
    }
}
