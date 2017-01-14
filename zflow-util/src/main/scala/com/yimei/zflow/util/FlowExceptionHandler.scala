package com.yimei.zflow.util

/**
  * Created by hary on 17/1/7.
  */

import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExceptionHandler
import com.yimei.zflow.util.HttpResult._
import com.yimei.zflow.util.config.Core
import com.yimei.zflow.util.exception.{BusinessException, DatabaseException}

/**
  * Created by wangqi on 16/12/27.
  */
trait FlowExceptionHandler extends SprayJsonSupport with Core {

  implicit def myExceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: BusinessException =>
      extractUri { uri =>
        log.error(s"Request to $uri could not be handled normally!!!!!!!!! BusinessException")
        complete(HttpResponse(StatusCodes.BadRequest, entity = Result(data = Some("error"), success = false, error = Some(Error(409, e.message, ""))).toJson.toString))
      }

    case e: DatabaseException =>
      extractUri { uri =>
        println(s"Request to $uri could not be handled normally!!!!!!!!! DatabaseException11111111 {}", e.message)
        complete(HttpResponse(StatusCodes.BadRequest, entity = e.message))
      }
    case e =>
      extractUri { uri =>
        log.error(s"Request to $uri could not be handled normally!!!!!!!!!")
        log.error("{}", e)
        complete(HttpResponse(StatusCodes.InternalServerError, entity = Result(data = Some("error"), success = false, error = Some(Error(500, "系统错误", ""))).toJson.toString))
      }
  }
}
