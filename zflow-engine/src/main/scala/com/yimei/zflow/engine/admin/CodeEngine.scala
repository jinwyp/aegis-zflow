package com.yimei.zflow.engine.admin

import java.io._
import java.nio.file.Paths
import java.nio.file.StandardOpenOption._
import java.util.{Map => JMap}
import scala.collection.JavaConversions.mapAsJavaMap

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Keep, Source}
import akka.util.ByteString
import com.yimei.zflow.api.models.flow.{Edge, TaskInfo}
import com.yimei.zflow.api.models.graph.{GraphConfig, GraphConfigProtocol, Vertex}
import freemarker.template.{Configuration, TemplateExceptionHandler}
import org.apache.commons.compress.archivers.tar.{TarArchiveEntry, TarArchiveOutputStream}
import org.apache.commons.io.FileUtils

import scala.concurrent.Future

/**
  * Created by hary on 17/1/10.
  */
object CodeEngine extends GraphConfigProtocol {

  import scala.concurrent.ExecutionContext.Implicits.global

  val ftlConfig = new Configuration(Configuration.VERSION_2_3_23);
  ftlConfig.setClassLoaderForTemplateLoading(this.getClass.getClassLoader, "code")
  ftlConfig.setDefaultEncoding("UTF-8");
  ftlConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
  ftlConfig.setLogTemplateExceptions(false);

  case class CodeMeta(groupId: String, artifact: String, entry: String)

  case class CodeConfig(points: JMap[String, String],
                        vertices: JMap[String, Vertex],
                        edges: JMap[String, Edge],
                        utasks: JMap[String, TaskInfo],
                        auto: JMap[String, TaskInfo]
                       )

  case class CodePoints(points: JMap[String, String])


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

    var dir: String = null
    var outputFile: String = null

    if (template == "Build.scala" || template == "plugins.sbt" || template == "build.properties" || template.endsWith(".conf")) {
      dir = root + File.separator + project + File.separator + "project"
      outputFile = dir + File.separator + template
    } else {

      // 看是main SourceSets 还是 test SourceSets
      val sets = if (template.endsWith("Test.ftl")) "test" else "main"
      val dirs: List[String] = root :: project :: "src" :: sets :: "scala" :: (
        meta.groupId.split("\\.").toList ++: (meta.artifact :: {
          if (entries.size == 1) Nil else entries.init
        }))
      dir = dirs.reduceLeft((a, b) => a + File.separator + b)
      outputFile = dir + File.separator + tdata.get("file")
    }
    FileUtils.forceMkdir(new File(dir))

    val fsrc: Source[ByteString, NotUsed] = Source.fromFuture(process(template, tdata).map(ByteString(_)))
    val sink = FileIO.toPath(Paths.get(outputFile), Set(CREATE, WRITE))
    val runnable = fsrc.toMat(sink)(Keep.right)

    runnable.run().map(result => (tdata.get("file"), result))
  }

  def genAll(gc: GraphConfig)(implicit system: ActorSystem): Future[(String, String)] = {
    import java.util.{HashMap => JMap}

    val httpExecutionContext = system.dispatcher
    implicit val materializer = ActorMaterializer()

    val meta = CodeMeta(gc.groupId, gc.artifact, gc.entry)
    def m(tpl: String, file: String, code: AnyRef) = (tpl, new JMap[String, AnyRef] {
      put("meta", meta)
      put("file", file)
      put("code", code)
    })
    def ms(tpl: String, filecodes: List[(String, AnyRef)]) =
      filecodes.map(e => (tpl, new JMap[String, AnyRef] {
        put("meta", meta)
        put("file", e._1)
        put("code", e._2)
      }))

    val all =
      m("Build.scala", "Build.scala", null) ::
      m("application.conf", "application.conf", null) ::
      m("persistence-leveldb.conf", "persistence-leveldb.conf", null) ::
      m("persistence-redis.conf", "persistence-redis.conf", null) ::
      m("build.properties", "build.properties", null) ::
      m("plugins.sbt", "pluings.sbt", null) ::
      m("XFlowApp.ftl", s"${gc.entry}App.scala", getAppCode(gc)) ::
      m("XFlowGraph.ftl", s"${gc.entry}Graph.scala", getGraphCode(gc)) ::
      m("utask.XFlowModels.ftl", "Models.scala", getModelsCode(gc)) ::
      m("config.XFlowConfig.ftl", s"${gc.entry}Config.scala", getConfigCode(gc)) ::
      m("config.XFlowPoints.ftl", s"${gc.entry}Points.scala", getPointsCode(gc)) ::
      m("utask.UTaskRoute.ftl", s"UTaskRoute.scala", getUTaskRouteCode(gc)) ::
      (ms("utask.XFlowTask.ftl", getUTaskCode(gc)) ++
       ms("utask.TaskTest.ftl", getUTaskTestCode(gc)))

    Future.traverse(all) { entry =>
      CodeEngine.genFile(entry._1, entry._2, "/tmp", s"aegis-zflow-${gc.artifact}")
    }.map { fs =>
      fs.foreach { case (filename, result) => println(s"${filename} => ${result.status}, ${result.count} bytes read.") }
      ("/tmp", s"aegis-zflow-${gc.artifact}")
    }
  }

  def getAppCode(graphConfig: GraphConfig) = null
  def getGraphCode(graphConfig: GraphConfig) = {
    CodeConfig(
      mapAsJavaMap(graphConfig.points),
      mapAsJavaMap(graphConfig.vertices),
      mapAsJavaMap(graphConfig.edges),
      mapAsJavaMap(graphConfig.userTasks),
      mapAsJavaMap(graphConfig.autoTasks)
    )
  }
  def getModelsCode(graphConfig: GraphConfig) = (null)
  def getConfigCode(graphConfig: GraphConfig) = (getGraphCode(graphConfig))
  def getPointsCode(graphConfig: GraphConfig) = CodePoints(mapAsJavaMap(graphConfig.points))
  def getUTaskCode(graphConfig: GraphConfig) = graphConfig.userTasks.keys.map(key => (s"Task${key}.scala" -> key)).toList
  def getUTaskTestCode(graphConfig: GraphConfig) = graphConfig.userTasks.keys.map(key => (s"Task${key}Test.scala" -> key)).toList
  def getUTaskRouteCode(graphConfig: GraphConfig) = graphConfig.userTasks.keys.toArray   // (Array("T1", "T2"))
}