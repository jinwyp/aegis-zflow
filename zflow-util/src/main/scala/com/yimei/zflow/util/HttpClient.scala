package com.yimei.zflow.util

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, _}
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.yimei.zflow.util.exception.BusinessException

import scala.concurrent.Future
import scala.concurrent.duration._


/**
  * Created by wangqi on 16/12/26.
  */

class HttpClient(endpoint: String)(implicit system: ActorSystem, materializer: ActorMaterializer) {

  import spray.json._

  implicit val ec = system.dispatcher

  /**
    *
    * @param path
    * @param paramters
    * @param pathVariables
    * @param model
    * @param method
    * @tparam T
    * @tparam R
    * @return
    */
  def request[T: JsonFormat, R: JsonFormat](
                                             path: String,
                                             paramters: Map[String, String] = Map(),
                                             pathVariables: Array[String] = Array(),
                                             model: Option[T] = None,
                                             method: String = "get"): Future[R] = {
    val httpMethod = method.toLowerCase match {
      case "get" => HttpMethods.GET
      case "post" => HttpMethods.POST
      case "put" => HttpMethods.PUT
      case "delete" => HttpMethods.DELETE
      case _ => throw new BusinessException("传入http方法有误")
    }

    val pathWithPathVariables = pathVariables.foldLeft(path)((p, pvb) => p + "/" + pvb)
    val paras = paramters.foldLeft("?")((p, pts) => p + pts._1 + "=" + pts._2 + "&")

    val fullUrl = paras.isEmpty match {
      case true => endpoint + pathWithPathVariables
      case false => endpoint + pathWithPathVariables + paras.substring(0, paras.length - 1)
    }

    import MediaTypes._

    //  val contentType = headers.`Content-Type`.apply(ContentType(`application/json`))

    val httpRequest = model match {
      case Some(a) => HttpRequest(uri = fullUrl, entity = HttpEntity(`application/json`, ByteString(a.toJson.toString, "UTF-8")), method = httpMethod)
      case _ => HttpRequest(uri = fullUrl, method = httpMethod)
    }


    //发送请求,并得到结果
    Http().singleRequest(
      httpRequest
    ) flatMap { r =>


      val strictEntity = r.entity.toStrict(10.seconds)
      val byteString: Future[ByteString] = strictEntity flatMap { e =>
        e.dataBytes
          .runFold(ByteString.empty) { case (acc, b) => acc ++ b }
      }
      val result: Future[String] = byteString map (_.decodeString("UTF-8"))

      //        byteString map {
      //        _.decodeString("UTF-8").toJson.convertTo[R]
      //      }

      r.status match {
        case StatusCodes.OK =>
          result map { r =>
            r.parseJson.convertTo[R]
          }
        case _ =>
          result map (r => throw new BusinessException(r.toString))
      }
    } recover {
      case BusinessException(r) =>
        throw new BusinessException(r)
      case e =>
        throw new BusinessException("网络异常")
    }
  }


  //发送报文,并取得回复
  def sendRequest( path: String,
                   paramters: Map[String, String] = Map(),
                   pathVariables: Array[String] = Array(),
                   bodyEntity: Option[String] = None,
                   method: String = "get"): Future[String] = {

    val httpMethod = method.toLowerCase match {
      case "get" => HttpMethods.GET
      case "post" => HttpMethods.POST
      case "put" => HttpMethods.PUT
      case "delete" => HttpMethods.DELETE
      case _ => throw new BusinessException("传入http方法有误")
    }

    val pathWithPathVariables = pathVariables.foldLeft(path)((p, pvb) => p + "/" + pvb)
    val paras = paramters.foldLeft("?")((p, pts) => p + pts._1 + "=" + pts._2 + "&")

    val fullUrl = paras.isEmpty match {
      case true => endpoint + pathWithPathVariables
      case false => endpoint + pathWithPathVariables + paras.substring(0, paras.length - 1)
    }

    import MediaTypes._


    //  val contentType = headers.`Content-Type`.apply(ContentType(`application/json`))

    val httpRequest = bodyEntity match {
      case Some(a) => HttpRequest(uri = fullUrl, entity = HttpEntity(`application/json`, ByteString(a, "UTF-8")), method = httpMethod)
      case _ => HttpRequest(uri = fullUrl, method = httpMethod)
    }


    //发送请求,并得到结果
    Http().singleRequest(
      httpRequest
    ) flatMap { r =>


      val strictEntity = r.entity.toStrict(10.seconds)
      val byteString: Future[ByteString] = strictEntity flatMap { e =>
        e.dataBytes
          .runFold(ByteString.empty) { case (acc, b) => acc ++ b }
      }
      val result = byteString map (_.decodeString("UTF-8"))

      r.status match {
        case StatusCodes.OK =>
          result
        case _ =>
          result map (r => throw new BusinessException(r))
      }
    } recover {
      case BusinessException(r) =>
        throw new BusinessException(r)
      case _ =>
        throw new BusinessException("网络异常")
    }
  }
}
