package com.yimei.zflow.util.id.serialization.json

import akka.serialization.SerializerWithStringManifest
import com.yimei.zflow.util.id.models._
import spray.json._


/**
  * Created by xl on 16/12/23.
  */
class IdSerialization extends SerializerWithStringManifest with IdGeneratorProtocol{
  override def identifier: Int = 4444

  val CommandGetIdManifest = classOf[CommandGetId].getName
  val IdManifest = classOf[Id].getName
  val EventIncreaseManifest = classOf[EventIncrease].getName
  val StateManifest = classOf[State].getName

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = manifest match {
    case CommandGetIdManifest => new String(bytes).parseJson.convertTo[CommandGetId]
    case IdManifest => new String(bytes).parseJson.convertTo[Id]
    case EventIncreaseManifest => new String(bytes).parseJson.convertTo[EventIncrease]
    case StateManifest => new String(bytes).parseJson.convertTo[State]
  }

  override def manifest(o: AnyRef): String = o.getClass.getName

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case e: CommandGetId => e.toJson.toString.getBytes
    case e: Id => e.toJson.toString.getBytes
    case e: EventIncrease => e.toJson.toString.getBytes
    case e: State => e.toJson.toString.getBytes
  }
}
