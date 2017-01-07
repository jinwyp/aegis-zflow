package com.yimei.zflow.util.asset.routes

import java.io.FileOutputStream
import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.Multipart.FormData.BodyPart
import akka.http.scaladsl.model.{Multipart, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.ByteString
import com.yimei.zflow.util.asset.db.AssetTable
import com.yimei.zflow.util.asset.db.Entities.AssetEntity
import com.yimei.zflow.util.asset.routes.Models.UploadResult
import com.yimei.zflow.util.config.CoreConfig
import com.yimei.zflow.util.exception.{BusinessException, DatabaseException}

import scala.concurrent.Future

trait AssetRoute extends CoreConfig with AssetTable with SprayJsonSupport {

  import driver.api._

  val fileRootPath = coreConfig.getString("file.root")

  import java.io.File

  import scala.concurrent.duration._

  /**
    * GET asset/:asset_id  -- 下载asset_id资源
    */
  def downloadFile: Route = get {
    path("file" / Segment) { id =>
      val file: Future[AssetEntity] = dbrun(assetClass.filter(f => f.asset_id === id).result.head) recover {
        case _ => throw new DatabaseException("该文件不存在")
      }
      val url = for {
        f <- file
      } yield f.url
      getFromFile(new File(fileRootPath + url))
      complete(StatusCodes.OK)
    }
  }

  /**
    * POST asset/    -- 上传文件:
    *
    * 1. username        --> 上传用户
    * 2. gid             --> 用户组
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

  def uploadFile: Route = post {
    path("file") {
      entity(as[Multipart.FormData]) { fileData =>
        // 多个文件
        val result: Future[Map[String, String]] = fileData.parts.mapAsync[(String, String)](1) {
          case file:
            BodyPart if file.name == "file" =>
            val uuId = UUID.randomUUID().toString
            val fileName = file.getFilename().get()
            processFile(uuId, fileName, fileData)
            Future("path" -> (uuId + fileName))
          case data: BodyPart => data.toStrict(2.seconds)
            .map(strict => data.name -> strict.entity.data.utf8String)
        }.runFold(Map.empty[String, String])((map, tuple) => map + tuple)

        def insert(data:Map[String,String]): Future[UploadResult] = {
          val path = data.get("path").get
          val busi_type = data.getOrElse("busi_type", "default")
          val description: String = data.getOrElse("description", "")
          val uuId = path.substring(0, 36)
          val originName = path.substring(36, path.length)
          val url = uuId.replace("-", "/") + "/" + originName
          val suffix = originName.substring(originName.lastIndexOf('.') + 1)
          val fileType = getFileType(suffix)
          val assetEntity: AssetEntity = new AssetEntity(None, uuId, fileType, busi_type, "username", Some("gid"), Some(description), url, originName, None)
          val temp: Future[Int] = dbrun( assetClass.insertOrUpdate(assetEntity)) recover {
            case _ => throw BusinessException(s"$url 上传失败")
          }
          temp.map(f=>UploadResult(uuId, originName))
        }
        complete(for{
          r <- result
          i <- insert(r)
        } yield {
          i
        })
      }
    }
  }

  def getFileType(suffix: String): Int = {
    val imageArray = Array("jpg", "jpeg", "gif", "png", "bmp")
    if (suffix.toLowerCase == "pdf") 1
    else if (imageArray.contains(suffix.toLowerCase)) 2
    else if (suffix.toLowerCase == "doc" || suffix.toLowerCase == "docx") 3
    else if (suffix.toLowerCase == "xls" || suffix.toLowerCase == "xlsx") 4
    else 0
  }

  private def processFile(uuId: String, fileOriginName: String, fileData: Multipart.FormData) {
    val dirPath = uuId.replace("-", "/")
    val newDir = new File(fileRootPath + dirPath)
    newDir.mkdirs()
    val fileOutput = new FileOutputStream(fileRootPath + "/" + dirPath + "/" + fileOriginName)
    fileData.parts.mapAsync(1) {
      bodyPart =>
        def writeFileOnLocal(array: Array[Byte], byteString: ByteString): Array[Byte] = {
          val byteArray: Array[Byte] = byteString.toArray
          fileOutput.write(byteArray)
          array ++ byteArray
        }
        bodyPart.entity.dataBytes.runFold(Array[Byte]())(writeFileOnLocal)
    }.runFold(0)(_ + _.length)
  }


  def route: Route = downloadFile ~ uploadFile
}

