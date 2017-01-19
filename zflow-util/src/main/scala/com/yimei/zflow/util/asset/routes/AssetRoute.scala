package com.yimei.zflow.util.asset.routes

import java.io.File
import java.nio.file.Paths
import java.nio.file.StandardOpenOption._
import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{ContentDispositionTypes, `Content-Disposition`}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.FileInfo
import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Keep, Source}
import akka.util.ByteString
import com.yimei.zflow.util.asset.db.AssetTable
import com.yimei.zflow.util.asset.db.Entities.AssetEntity
import com.yimei.zflow.util.asset.routes.Models._
import com.yimei.zflow.util.config.Core
import com.yimei.zflow.util.exception.{BusinessException, DatabaseException}
import com.yimei.zflow.util.organ.{OrganSession, Session}
import org.apache.commons.io.FileUtils

import scala.concurrent.Future

trait AssetRoute extends Core with AssetTable with SprayJsonSupport with Session {

  val fileField: String

  implicit val assetRouteExecutionContext = coreSystem.dispatcher

  import driver.api._

  val fileRoot = "/tmp" // coreSystem.settings.config.getString("file.root")

  private def getAssetInfo(id: String): Future[(String, String, Source[ByteString, Future[IOResult]])] = {
    val query = assetClass.filter(f => f.asset_id === id).map { e =>
      (e.url, e.file_type, e.origin_name)
    }.result.head

    dbrun(query).recover {
      case _ =>
        log.error("Database error in downloadFile")
        throw new DatabaseException("该文件不存在")
    } map {
      case (url, fileType, name) =>
        (fileType, name, FileIO.fromPath(Paths.get(fileRoot + File.separator + url)))
    }
  }

  /**
    * GET asset/:asset_id  -- 下载asset_id资源
    */
  private def downloadFile: Route = get {
    path("download" / Segment) { id =>
      onSuccess(getAssetInfo(id)) {
        case (_, name, source) =>
          complete(
            HttpResponse(
              status = StatusCodes.OK,
              headers = List(`Content-Disposition`(ContentDispositionTypes.attachment, Map("filename" -> name))),
              entity = HttpEntity(`application/octet-stream`, source)
            )
          )
      }
    }
  }

  /**
    * POST asset/    -- 上传文件:
    *
    * 1. username        --> 上传用户
    * 3. file_type       --> 后台计算   暂时依据后缀判断
    * 4. busi_type       --> 表单
    * 5. uri             --> 存储位置
    *
    * 算法:
    * 1. 保持文件到文件系统, 记录位置为uri   --->    uuid  $file_root/xxxx/xx/xxx/xxx.pdf
    * 2. 组织1, 2, 3, 4,5信息, 保存到数据库记录
    *
    * @return
    */

  private def uploadFile: Route = path("upload") {
    post {
      extractRequestContext { ctx =>
        implicit val materializer = ctx.materializer
        (organOptionalSession & parameter('busiType.?)) { (session, busiType) =>

          println("got busiType => " + busiType)
          println("got session => " + session)

          fileUpload(fileField) {
            case (meta: FileInfo, source: Source[ByteString, Any]) =>
              // d33d2a10-1def-4e87-9fb7-dd629ecad8b0
              val regex = "(\\w+)-(\\w+)-(\\w+)-(\\w+)-(\\w+)".r
              val (dir, file, asset_id) = UUID.randomUUID().toString match {
                case id@regex(a, b, c, d, e) => (
                  a + File.separator +
                    b + File.separator +
                    c + File.separator +
                    d + File.separator,
                  e,
                  id
                  )
              }
              println("asset id is " + asset_id)
              val gdir = fileRoot + File.separator + dir
              FileUtils.forceMkdir(new File(gdir))

              val username = session match {
                case None => "hello"
                case k => k.get.username
              }

              val sink = FileIO.toPath(Paths.get(gdir + File.separator + file), Set(CREATE, WRITE))
              val assetEntity = new AssetEntity(
                None,
                asset_id,
                meta.contentType.toString(),
                busiType.getOrElse("0"),
                username,
                None,
                dir + File.separator + file,
                meta.fileName,
                None)

              val ff = for {
                result <- source.toMat(sink)(Keep.right).run
                insert <- dbrun(assetClass.insertOrUpdate(assetEntity)) recover {
                  case _ =>
                    log.error("upload failed")
                    throw BusinessException(s"${meta.fileName} 上传失败")
                }
              } yield (result, insert)

              onSuccess(ff) {
                case (result, insert) =>
                  log.info(s"upload file[${meta.fileName}] size[${result.count}] status[${result.status}]")
                  complete(UploadResult(assetEntity.url))
              }
          }
        }
      }
    }
  }

  private def uploadFile2: Route = path("upload2") {
    post {
      extractRequestContext { ctx =>
        implicit val materializer = ctx.materializer
        parameter('busiType.?) { busiType =>
          fileUpload(fileField) {
            case (meta: FileInfo, source: Source[ByteString, Any]) =>
              // d33d2a10-1def-4e87-9fb7-dd629ecad8b0
              val regex = "(\\w+)-(\\w+)-(\\w+)-(\\w+)-(\\w+)".r
              val (dir, file, asset_id) = UUID.randomUUID().toString match {
                case id@regex(a, b, c, d, e) => (
                  a + File.separator +
                    b + File.separator +
                    c + File.separator +
                    d + File.separator,
                  e,
                  id
                  )
              }
              println("asset id is " + asset_id)
              val gdir = fileRoot + File.separator + dir
              FileUtils.forceMkdir(new File(gdir))

              val sink = FileIO.toPath(Paths.get(gdir + File.separator + file), Set(CREATE, WRITE))
              val assetEntity = new AssetEntity(
                None,
                asset_id,
                meta.contentType.toString(),
                busiType.getOrElse("0"),
                "hary", //session.username,
                None,
                dir + File.separator + file,
                meta.fileName,
                None)

              val ff = for {
                result <- source.toMat(sink)(Keep.right).run
                insert <- dbrun(assetClass.insertOrUpdate(assetEntity)) recover {
                  case _ =>
                    log.error("upload failed")
                    throw BusinessException(s"${meta.fileName} 上传失败")
                }
              } yield (result, insert)

              onSuccess(ff) {
                case (result, insert) =>
                  log.info(s"upload file[${meta.fileName}] size[${result.count}] status[${result.status}]")
                  complete(UploadResult(assetEntity.url))
              }
          }
        }
      }
    }
  }

  def assetLogin: Route = path("login") {
    organSetSession(OrganSession("hary", "uid", "party", "instanceId", "company")) { ctx =>
      ctx.complete("ok")
    }
  }

  def assetRoute: Route = downloadFile ~ uploadFile ~ uploadFile2 ~ assetLogin
}

