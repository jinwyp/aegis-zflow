package com.yimei.zflow.util

import spray.json.{DefaultJsonProtocol, JsonFormat}

/**
  * Created by hary on 17/1/7.
  */
object HttpResult extends DefaultJsonProtocol {

  case class Result[T](data: Option[T], success: Boolean = true, error: Error = null, meta: PagerInfo = null)

  case class PagerInfo(total: Int, count: Int, offset: Int, page: Int)

  implicit val PagerInfoFormat = jsonFormat4(PagerInfo)

  case class Error(code: Int, message: String, field: String)

  implicit val ErrorFormat = jsonFormat3(Error)

  implicit def resultFormat[A: JsonFormat] = jsonFormat4(Result.apply[A])
}
