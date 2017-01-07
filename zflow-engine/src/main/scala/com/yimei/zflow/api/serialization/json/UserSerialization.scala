package com.yimei.zflow.api.serialization.json

import akka.serialization.SerializerWithStringManifest
import com.yimei.zflow.api.models.user._
import spray.json._

/**
  * Created by xl on 16/12/23.
  */
class UserSerialization extends SerializerWithStringManifest with UserProtocol {
  override def identifier: Int = 5555

  val CommandCreateUserManifest = classOf[CommandCreateUser].getName
  val CommandTaskSubmitManifest = classOf[CommandTaskSubmit].getName
  val CommandShutDownManifest = classOf[CommandShutDown].getName
//  val CommandMobileComeManifest = classOf[CommandMobileCome].getName
//  val CommandDesktopComeManifest = classOf[CommandDesktopCome].getName
  val CommandQueryUserManifest = classOf[CommandQueryUser].getName
  val CommandUserTaskManifest = classOf[CommandUserTask].getName
  val TaskEnqueueManifest = classOf[TaskEnqueue].getName
  val TaskDequeueManifest = classOf[TaskDequeue].getName
  val StateManifest = classOf[State].getName

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = manifest match {
    case CommandCreateUserManifest => new String(bytes).parseJson.convertTo[CommandCreateUser]
    case CommandTaskSubmitManifest => new String(bytes).parseJson.convertTo[CommandTaskSubmit]
    case CommandShutDownManifest => new String(bytes).parseJson.convertTo[CommandShutDown]
//    case CommandMobileComeManifest => new String(bytes).parseJson.convertTo[CommandMobileCome]
//    case CommandDesktopComeManifest => new String(bytes).parseJson.convertTo[CommandDesktopCome]
    case CommandQueryUserManifest => new String(bytes).parseJson.convertTo[CommandQueryUser]
    case CommandUserTaskManifest => new String(bytes).parseJson.convertTo[CommandUserTask]
    case TaskEnqueueManifest => new String(bytes).parseJson.convertTo[TaskEnqueue]
    case TaskDequeueManifest => new String(bytes).parseJson.convertTo[TaskDequeue]
    case StateManifest => new String(bytes).parseJson.convertTo[State]
  }

  override def manifest(o: AnyRef): String = o.getClass.getName

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case e: CommandCreateUser => e.toJson.toString.getBytes
    case e: CommandTaskSubmit => e.toJson.toString.getBytes
    case e: CommandShutDown => e.toJson.toString.getBytes
//    case e: CommandMobileCome => e.toJson.toString.getBytes
//    case e: CommandDesktopCome => e.toJson.toString.getBytes
    case e: CommandQueryUser => e.toJson.toString.getBytes
    case e: CommandUserTask => e.toJson.toString.getBytes
    case e: TaskEnqueue => e.toJson.toString.getBytes
    case e: TaskDequeue => e.toJson.toString.getBytes
    case e: State => e.toJson.toString.getBytes
  }
}

