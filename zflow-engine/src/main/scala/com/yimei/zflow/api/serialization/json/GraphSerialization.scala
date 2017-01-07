package com.yimei.zflow.api.serialization.json

import akka.serialization.SerializerWithStringManifest
import com.yimei.zflow.api.models.graph._
import spray.json._

/**
  * Created by xl on 16/12/23.
  */
class GraphSerialization extends SerializerWithStringManifest with GraphConfigProtocol {
  override def identifier: Int = 2222

  val VertexManifest = classOf[Vertex].getName
  val GraphConfigManifest = classOf[GraphConfig].getName

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = manifest match {
    case VertexManifest => new String(bytes).parseJson.convertTo[Vertex]
    case GraphConfigManifest => new String(bytes).parseJson.convertTo[GraphConfig]
  }

  override def manifest(o: AnyRef): String = o.getClass.getName

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case e: Vertex => e.toJson.toString.getBytes
    case e: GraphConfig => e.toJson.toString.getBytes
  }
}
