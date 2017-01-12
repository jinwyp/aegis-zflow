package ${meta.groupId()}.${meta.artifact()}.config

import com.wix.accord.Validator
import com.wix.accord.dsl._
import spray.json.DefaultJsonProtocol

object ${meta.entry()}Points extends DefaultJsonProtocol {

<#list code.points()?keys as point>
  case class Point${point}(name: String)
  implicit val pointWifeFormat = jsonFormat1(Point${point})
</#list>

  // validations
  object Validation {
<#list code.points()?keys as point>
    implicit val Point${point}Validate: Validator[Point${point}] = validator[Point${point}] { pt =>
      pt.name.length as "长度"  is between(1,200)
    }
</#list>
  }
}