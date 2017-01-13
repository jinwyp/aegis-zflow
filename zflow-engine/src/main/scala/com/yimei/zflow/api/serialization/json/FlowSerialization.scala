package com.yimei.zflow.api.serialization.json

import akka.serialization.SerializerWithStringManifest
import com.yimei.zflow.api.models.flow._
import spray.json.DefaultJsonProtocol
import spray.json._

/**
  * Created by xl on 16/12/23.
  */
class FlowSerialization extends SerializerWithStringManifest with FlowProtocol {
  override def identifier: Int = 1111

  val DataPointManifest = classOf[DataPoint].getName
  val CommandCreateFlowManifest = classOf[CommandCreateFlow].getName
  val CreateFlowSuccessManifest = classOf[CreateFlowSuccess].getName
  val CommandRunFlowManifest = classOf[CommandRunFlow].getName
  val CommandShutdownManifest = classOf[CommandShutdown].getName
  val CommandPointManifest = classOf[CommandPoint].getName
  val CommandPointsManifest = classOf[CommandPoints].getName
  val CommandFlowGraphManifest = classOf[CommandFlowGraph].getName
  val CommandFlowStateManifest = classOf[CommandFlowState].getName
  val CommandUpdatePointsManifest = classOf[CommandUpdatePoints].getName
  val CommandHijackManifest = classOf[CommandHijack].getName
  val PointUpdatedManifest = classOf[PointUpdated].getName
  val PointsUpdatedManifest = classOf[PointsUpdated].getName
  val EdgeCompletedManifest = classOf[EdgeCompleted].getName
  val DecisionUpdatedManifest = classOf[DecisionUpdated].getName
  val HijackedManifest = classOf[Hijacked].getName
  val StateManifest = classOf[State].getName
  val PartUTaskManifest = classOf[PartUTask].getName
  val PartGTaskManifest = classOf[PartGTask].getName
  val EdgeManifest = classOf[Edge].getName
  val TaskInfoManifest = classOf[TaskInfo].getName
  val GraphManifest = classOf[Graph].getName
  val ArrowManifest = classOf[Arrow].getName
  val VertexManifest = classOf[Vertex].getName
  val GraphConfigManifest = classOf[GraphConfig].getName

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = manifest match {
    case DataPointManifest => new String(bytes).parseJson.convertTo[DataPoint]
    case CommandCreateFlowManifest => new String(bytes).parseJson.convertTo[CommandCreateFlow]
    case CreateFlowSuccessManifest => new String(bytes).parseJson.convertTo[CreateFlowSuccess]
    case CommandRunFlowManifest => new String(bytes).parseJson.convertTo[CommandRunFlow]
    case CommandShutdownManifest => new String(bytes).parseJson.convertTo[CommandShutdown]
    case CommandPointManifest => new String(bytes).parseJson.convertTo[CommandPoint]
    case CommandPointsManifest => new String(bytes).parseJson.convertTo[CommandPoints]
    case CommandFlowGraphManifest => new String(bytes).parseJson.convertTo[CommandFlowGraph]
    case CommandFlowStateManifest => new String(bytes).parseJson.convertTo[CommandFlowState]
    case CommandUpdatePointsManifest => new String(bytes).parseJson.convertTo[CommandUpdatePoints]
    case CommandHijackManifest => new String(bytes).parseJson.convertTo[CommandHijack]
    case PointUpdatedManifest => new String(bytes).parseJson.convertTo[PointUpdated]
    case PointsUpdatedManifest => new String(bytes).parseJson.convertTo[PointsUpdated]
    case EdgeCompletedManifest => new String(bytes).parseJson.convertTo[EdgeCompleted]
    case DecisionUpdatedManifest => new String(bytes).parseJson.convertTo[DecisionUpdated]
    case HijackedManifest => new String(bytes).parseJson.convertTo[Hijacked]
    case StateManifest => new String(bytes).parseJson.convertTo[State]
    case PartUTaskManifest => new String(bytes).parseJson.convertTo[PartUTask]
    case PartGTaskManifest => new String(bytes).parseJson.convertTo[PartGTask]
    case EdgeManifest => new String(bytes).parseJson.convertTo[Edge]
    case TaskInfoManifest => new String(bytes).parseJson.convertTo[TaskInfo]
    case GraphManifest => new String(bytes).parseJson.convertTo[Graph]
    case ArrowManifest => new String(bytes).parseJson.convertTo[Arrow]
    case VertexManifest => new String(bytes).parseJson.convertTo[Vertex]
    case GraphConfigManifest => new String(bytes).parseJson.convertTo[GraphConfig]
  }

  override def manifest(o: AnyRef): String = o.getClass.getName

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case e: DataPoint => e.toJson.toString.getBytes
    case e: CommandCreateFlow => e.toJson.toString.getBytes
    case e: CreateFlowSuccess => e.toJson.toString.getBytes
    case e: CommandRunFlow => e.toJson.toString.getBytes
    case e: CommandShutdown => e.toJson.toString.getBytes
    case e: CommandPoint => e.toJson.toString.getBytes
    case e: CommandPoints => e.toJson.toString.getBytes
    case e: CommandFlowGraph => e.toJson.toString.getBytes
    case e: CommandFlowState => e.toJson.toString.getBytes
    case e: CommandUpdatePoints => e.toJson.toString.getBytes
    case e: CommandHijack => e.toJson.toString.getBytes
    case e: PointUpdated => e.toJson.toString.getBytes
    case e: PointsUpdated => e.toJson.toString.getBytes
    case e: EdgeCompleted => e.toJson.toString.getBytes
    case e: DecisionUpdated => e.toJson.toString.getBytes
    case e: Hijacked => e.toJson.toString.getBytes
    case e: State => e.toJson.toString.getBytes
    case e: PartUTask => e.toJson.toString.getBytes
    case e: PartGTask => e.toJson.toString.getBytes
    case e: Edge => e.toJson.toString.getBytes
    case e: TaskInfo => e.toJson.toString.getBytes
    case e: Graph => e.toJson.toString.getBytes
    case e: Arrow => e.toJson.toString.getBytes
    case e: Vertex => e.toJson.toString.getBytes
    case e: GraphConfig => e.toJson.toString.getBytes
  }
}
