package com.yimei.zflow.util.asset.routes

import spray.json.DefaultJsonProtocol

/**
  * Created by hary on 17/1/7.
  */
object Models extends  DefaultJsonProtocol {
  case class UploadResult(id: String)
  implicit val uploadResultFormat = jsonFormat1(UploadResult)

  val defaultType = 0
  val pdfType = 1
  val pngType = 2
  val jpgType = 3
}
