package com.yimei.zflow.api

import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

import com.yimei.zflow.api.models.flow.DataPoint
import spray.json._


object PointUtil {

  case class PointModel[T](value: T, memo: Option[String], operator: Option[String], id: String, timestamp: Long)
  /**
    * Created by hary on 16/12/15.
    */
  implicit class str2object(str: String) {
    def as[A: JsonFormat] = str.parseJson.convertTo[A]
  }

  implicit class object2str[A:JsonFormat](o: A){
    def str: String = if ( o.isInstanceOf[String]) o.asInstanceOf[String] else o.toJson.toString()
  }

  implicit class dataPointUnWrapper(dp: DataPoint) {
    def unwrap[A:JsonFormat] = PointModel[A]( dp.value.parseJson.convertTo[A], dp.memo, dp.operator, dp.id,
      dp.timestamp)
  }

  implicit class dataPointWrapper[A:JsonFormat](a: A) {
    def wrap(memo: Option[String] = None, operator: Option[String] = None) =
      DataPoint(a.str, memo, operator, UUID.randomUUID().toString, Timestamp.from(Instant.now()).getTime, false)
  }
}
