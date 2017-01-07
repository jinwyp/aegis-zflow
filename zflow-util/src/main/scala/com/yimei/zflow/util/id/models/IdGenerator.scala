package com.yimei.zflow.util.id.models

import spray.json.DefaultJsonProtocol

// Command
trait Command
case class CommandGetId(key: String, buffer: Int = 1) extends Command
case object CommandQueryId extends Command

// 返回的id
case class Id(id: Long)

// Event
trait Event

case class EventIncrease(key: String, buffer: Int) extends Event

// State
case class State(keys: Map[String, Long])

// json protocol
trait IdGeneratorProtocol extends DefaultJsonProtocol {
  implicit val commandGetIdFormat = jsonFormat2(CommandGetId)
  implicit val idFormat = jsonFormat1(Id)
  implicit val eventIncreaseFormat = jsonFormat2(EventIncrease)
  implicit val idGeneratorStateFormat = jsonFormat1(State)
}

