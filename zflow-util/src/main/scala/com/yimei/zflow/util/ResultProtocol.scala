package com.yimei.zflow.util

import com.yimei.zflow.util.exception.BusinessException
import spray.json.{DefaultJsonProtocol, JsArray, JsNumber, JsObject, JsString, JsValue, JsonFormat, RootJsonFormat}

/**
  * Created by hary on 17/1/7.
  */
object ResultProtocol extends DefaultJsonProtocol {

  case class Result[T](data: Option[T], success: Boolean = true, error: Error = null, meta: PagerInfo = null)

  case class PagerInfo(total: Int, count: Int, offset: Int, page: Int)

  case class Error(code: Int, message: String, field: String)


  implicit object PagerInfoFormat extends RootJsonFormat[PagerInfo] {
    override def write(obj: PagerInfo): JsValue = {
      if (obj == null) {
        JsString("")
      } else {
        JsObject(("total", JsNumber(obj.total)), ("count", JsNumber(obj.count)), ("offset", JsNumber(obj.offset)), ("page", JsNumber(obj.page)))
      }
    }

    override def read(json: JsValue): PagerInfo = json match {
      case JsArray(Vector(JsNumber(total), JsNumber(count), JsNumber(offset), JsNumber(page))) =>
        PagerInfo(total.toInt, count.toInt, offset.toInt, page.toInt)
      case _ => throw new BusinessException("Meta 序列化错误")
    }
  }

  implicit object errFormat extends RootJsonFormat[Error] {
    override def write(obj: Error): JsValue = {
      if (obj == null) {
        JsString("")
      } else {
        JsArray(JsNumber(obj.code), JsString(obj.message), JsString(obj.field))
      }
    }

    override def read(json: JsValue): Error = json match {
      case JsArray(Vector(JsNumber(code), JsString(message), JsString(field))) =>
        Error(code.toInt, message, field)
      case _ => throw new BusinessException("error 序列化错误")
    }
  }

  //implicit def metaFormat = jsonFormat4(Meta)
  //implicit def errorFormat = jsonFormat3(Error)
  implicit def resultFormat[A: JsonFormat] = jsonFormat4(Result.apply[A])

}
