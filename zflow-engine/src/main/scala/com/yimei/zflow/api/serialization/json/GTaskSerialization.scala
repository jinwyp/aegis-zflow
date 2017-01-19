package com.yimei.zflow.api.serialization.json

import akka.serialization.SerializerWithStringManifest
import com.yimei.zflow.api.models.gtask._
import spray.json._

/**
  * Created by xl on 16/12/23.
  */
class GTaskSerialization extends SerializerWithStringManifest with GTaskProtocol {
  override def identifier: Int = 3333

  val CommandCreateGroupManifest = classOf[CommandCreateGroup].getName
  val CommandGroupTaskManifest = classOf[CommandGroupTask].getName
  val CommandClaimTaskManifest = classOf[CommandClaimTask].getName
  val CommandQueryGroupManifest = classOf[CommandQueryGroup].getName
  val TaskEnqueueManifest = classOf[TaskEnqueue].getName
  val TaskDequeueManifest = classOf[TaskDequeue].getName
  val StateManifest = classOf[State].getName

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = manifest match {
    case CommandCreateGroupManifest => new String(bytes).parseJson.convertTo[CommandCreateGroup]
    case CommandGroupTaskManifest => new String(bytes).parseJson.convertTo[CommandGroupTask]
    case CommandClaimTaskManifest => new String(bytes).parseJson.convertTo[CommandClaimTask]
    case CommandQueryGroupManifest => new String(bytes).parseJson.convertTo[CommandQueryGroup]
    case TaskEnqueueManifest => new String(bytes).parseJson.convertTo[TaskEnqueue]
    case TaskDequeueManifest => new String(bytes).parseJson.convertTo[TaskDequeue]
    case StateManifest => new String(bytes).parseJson.convertTo[State]}

  override def manifest(o: AnyRef): String = o.getClass.getName

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case e: CommandCreateGroup => e.toJson.toString.getBytes
    case e: CommandGroupTask => e.toJson.toString.getBytes
    case e: CommandClaimTask => e.toJson.toString.getBytes
    case e: CommandQueryGroup => e.toJson.toString.getBytes
    case e: TaskEnqueue => e.toJson.toString.getBytes
    case e: TaskDequeue => e.toJson.toString.getBytes
    case e: State => e.toJson.toString.getBytes
  }

}
