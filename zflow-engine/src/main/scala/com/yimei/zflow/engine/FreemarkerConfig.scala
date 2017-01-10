package com.yimei.zflow.engine

import java.io.{ByteArrayOutputStream, OutputStreamWriter}

import akka.http.scaladsl.model.ContentTypes.`text/html(UTF-8)`
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.Source
import akka.util.ByteString
import freemarker.template.{Configuration, TemplateExceptionHandler}

import scala.concurrent.Future

/**
  * Created by hary on 17/1/10.
  */
object FreemarkerConfig {

}
