package com.yimei.zflow.engine.admin

import java.io.{ByteArrayOutputStream, File, OutputStreamWriter}
import java.nio.file.Paths
import java.nio.file.StandardOpenOption._
import java.util.{HashMap => JMap}

import akka.NotUsed
import akka.stream.scaladsl.{FileIO, Keep, Source}
import akka.stream.{ActorMaterializer, IOResult}
import akka.util.ByteString
import com.yimei.zflow.api.models.flow.{Edge, TaskInfo}
import freemarker.template.{Configuration, TemplateExceptionHandler}
import org.apache.commons.io.FileUtils

import scala.concurrent.Future

/**
  * Created by hary on 17/1/10.
  */
object CodeEngine {

  import scala.concurrent.ExecutionContext.Implicits.global

  val ftlConfig = new Configuration(Configuration.VERSION_2_3_23);
  ftlConfig.setClassLoaderForTemplateLoading(this.getClass.getClassLoader, "code")
  ftlConfig.setDefaultEncoding("UTF-8");
  ftlConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
  ftlConfig.setLogTemplateExceptions(false);

  case class CodeMeta(groupId: String, artifact: String, entry: String, filename: String)
  case class CodeConfig( points: JMap[String, String],
                         vertices: JMap[String, String],
                         edges: JMap[String, Edge],
                         utasks: JMap[String, TaskInfo],
                        auto: JMap[String, TaskInfo]
                       )
  case class CodePoints(points: JMap[String, String])

  /*
  package #{groupId}.#{artifact}.config

  object #{entry}Config {

    // points
    val point_#{point} = "#{point}"  //  #{desc}

    // vertices
    val vertex_#{vertex} = "#{vertex}"  // #{desc}

    // edges
    val edge_#{edge} = "#{edge}"

    // user tasks
    val utask_#{utask}  = "#{utask}"

    // auto tasks
    val auto_#{auto} = "#{auto}"

  }
  */

  def process(template: String, tdata: JMap[String, AnyRef]): Future[String] = {
    Future {
      val os = new ByteArrayOutputStream()
      val out = new OutputStreamWriter(os)
      ftlConfig.getTemplate(template).process(tdata, out)
      os.toString
    }
  }

  def genFile(template: String, tdata: JMap[String, AnyRef], root: String, project: String)(implicit materializer: ActorMaterializer) = {
    val meta = tdata.get("meta").asInstanceOf[CodeMeta]
    val entries = template.split("\\.").toList.init
    val dirs: List[String] = root :: project :: "src" :: "main" :: "scala" :: (
      meta.groupId.split("\\.").toList ++: (meta.artifact :: {
        if (entries.size == 1) Nil else entries.init
      })
      )
    val dir = dirs.reduceLeft((a, b) => a + File.separator + b)
    val outputFile = dir + File.separator + meta.filename
      FileUtils.forceMkdir(new File(dir))

    val fsrc: Source[ByteString, NotUsed] = Source.fromFuture(process(template, tdata).map(ByteString(_)))
    val sink = FileIO.toPath(Paths.get(outputFile), Set(CREATE, WRITE))
    val runnable = fsrc.toMat(sink)(Keep.right)

    runnable.run().map(result => (meta.filename, result))
  }

//  def genAll() = {
//    val models = List(
//      new JMap[String, AnyRef](){ put("meta", null); put("models", models)}
//    )
//    // Future.traverse()
//  }
}
