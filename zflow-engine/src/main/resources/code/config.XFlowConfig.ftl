package ${meta.groupId()}.${meta.artifact()}.config

/**
 * code generated at <>
 */
object ${meta.entry()}Config {

  // points
<#list code.points()?keys as key>
  val point_${key} = "${key}"  //  ${code.points()[key]}
</#list>

  // vertices
<#list code.vertices()?keys as key>
  val vertex_${key} = "${key}"  // ${code.vertices()[key].description()}
</#list>

  // edges-->
<#list code.edges()?keys as key>
  val edge_${key} = "${key}"
</#list>

  // user tasks
<#list code.utasks()?keys as key>
  val utask_${key}  = "${key}"  // ${code.utasks()[key].description()}
</#list>

  // auto tasks
<#list code.auto()?keys as key>
  val auto_${key} = "${key}" // ${code.auto()[key].description()}
</#list>

}

