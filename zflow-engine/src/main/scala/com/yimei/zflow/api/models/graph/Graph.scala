package com.yimei.zflow.api.models.graph

import com.yimei.zflow.api.models.flow._
import spray.json.DefaultJsonProtocol


case class Vertex(description: String, next: Option[Arrow])

case class GraphConfig(
                        groupId: String,
                        artifact: String,
                        entry: String,
                        persistent: Boolean,
                        timeout: Int,
                        initial: String,
                        points: Map[String, String],
                        autoTasks: Map[String, TaskInfo],
                        userTasks: Map[String, TaskInfo],
                        vertices: Map[String, Vertex],
                        edges: Map[String, Edge]
                      )

trait GraphConfigProtocol extends FlowProtocol with DefaultJsonProtocol {
  implicit val defaultVertexFormat = jsonFormat2(Vertex)
  implicit val graphConfigProtocolFormat = jsonFormat11(GraphConfig)
}
