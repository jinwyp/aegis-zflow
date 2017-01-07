package com.yimei.zflow.util

import java.sql.Timestamp
import java.text.SimpleDateFormat

import spray.json.{DeserializationException, JsString, JsValue, RootJsonFormat}

/**
  * Created by hary on 17/1/7.
  */

trait CommonJsonFormat {

  implicit object SqlTimestampFormat extends RootJsonFormat[Timestamp] {
    val formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    override def write(obj: Timestamp) = JsString(formatter.format(obj))

    override def read(json: JsValue) : Timestamp = json match {
      case JsString(s) => new Timestamp(formatter.parse(s).getTime)
      case _ => throw new DeserializationException("Error info you want here ...")
    }
  }
}
