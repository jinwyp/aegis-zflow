package com.yimei.zflow.util

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server._
import akka.http.scaladsl.model.StatusCodes._
import Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.yimei.zflow.util.config.Core

/**
  * Created by wangqi on 17/1/18.
  */
trait FlowRejectionHandler extends SprayJsonSupport with Core {

  implicit def myRejectionHandler =
    RejectionHandler.newBuilder()
      .handle { case MissingCookieRejection(cookieName) =>
        complete(HttpResponse(BadRequest, entity = "No cookies, no service!!!"))
      }
      .handle { case AuthorizationFailedRejection =>
        complete((Forbidden, "You're out of your depth!"))
      }
      .handle { case ValidationRejection(msg, _) =>
        complete((InternalServerError, "That wasn't valid! " + msg))
      }
      .handleAll[MethodRejection] { methodRejections =>
      val names = methodRejections.map(_.supported.name)
      complete((MethodNotAllowed, s"Can't do that! Supported: ${names mkString " or "}!"))
    }
      .handleNotFound { complete("Not here!") }
      .result()
}
