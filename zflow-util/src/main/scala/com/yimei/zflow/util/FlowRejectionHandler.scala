package com.yimei.zflow.util

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server._
import akka.http.scaladsl.model.StatusCodes._
import Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.yimei.zflow.util.HttpResult.{Error, Result}
import com.yimei.zflow.util.config.Core

/**
  * Created by wangqi on 17/1/18.
  */
trait FlowRejectionHandler extends SprayJsonSupport with Core {

  implicit def myRejectionHandler =
    RejectionHandler.newBuilder()
      .handle { case MissingCookieRejection(cookieName) =>
        extractUri{ uri =>
          log.warning(s"$uri No cookies, no service!!!")
          complete(HttpResponse(BadRequest, entity = "No cookies, no service!!!"))
        }
      }
      .handle { case MissingQueryParamRejection(name) =>
        extractUri { uri =>
          log.warning(s"$uri miss request parameter $name")
          complete((Forbidden, s"miss request parameter $name"))
        }
      }
      .handle { case AuthorizationFailedRejection =>
        extractUri { uri =>
          log.warning(s"$uri You're out of your depth!")
          complete((Forbidden, "You're out of your depth!"))
        }
      }
      .handle { case ValidationRejection(msg, _) =>
        extractUri { uri =>
          log.warning(s"$uri That wasn't valid! {}",msg)
          complete((InternalServerError, "That wasn't valid! " + msg))
        }
      }
      .handleAll[MethodRejection] { methodRejections =>
      extractUri { uri =>
        val names = methodRejections.map(_.supported.name)
        log.warning("{} Can't do that! Supported: {} ",uri,names)
        complete((MethodNotAllowed, s"Can't do that! Supported: ${names mkString " or "}!"))
      }
    }
      .handleNotFound {
        complete(HttpResponse(StatusCodes.NotFound, entity = "Not found"))
      }
      .result()
}
