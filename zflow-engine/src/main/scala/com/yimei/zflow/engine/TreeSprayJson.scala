package com.yimei.zflow.engine

import com.yimei.zflow.api.models.flow.FlowProtocol
import spray.json.{JsValue, JsonFormat}

/**
  *
  * import GraphBuilder._
  * GraphBuilder.create(state){ implicit builder =>
  * V1 ~> E3 ~> V4
  * V1 ~> E1 ~> V2
  * V4 ~> E4 ~> V5
  * V4 ~> E5 ~> V6
  * V2 ~> E2 ~> V3
  * builder
  * }
  */


object TreeSprayJson extends FlowProtocol {

  trait Tree

  case class Node(value: String, children: List[Tree]) extends Tree

  case class Leaf(value: String) extends Tree

  implicit object TreeFormat extends JsonFormat[Tree] {
    def write(obj: Tree) = {
      obj match {
        case n: Node => nodeFormat.write(n)
        case l: Leaf => leafFormat.write(l)
      }
    }

    def read(jsValue: JsValue) = ???  // 不实现
  }

  implicit val nodeFormat = jsonFormat2(Node)
  implicit val leafFormat = jsonFormat1(Leaf)
}
