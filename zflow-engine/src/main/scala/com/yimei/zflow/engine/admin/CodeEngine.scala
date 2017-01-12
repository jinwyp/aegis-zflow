package com.yimei.zflow.engine.admin

import java.io.{ByteArrayOutputStream, File, OutputStreamWriter}
import java.nio.file.Paths
import java.nio.file.StandardOpenOption._
import java.util.{HashMap => JMap}

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Keep, Source}
import akka.util.ByteString
import com.yimei.zflow.api.models.flow.{Edge, TaskInfo}
import com.yimei.zflow.api.models.graph.{GraphConfig, GraphConfigProtocol}
import freemarker.template.{Configuration, TemplateExceptionHandler}
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
                        vertices: JMap[String, String],
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

    val sets = if (template.endsWith("Test.ftl")) "test" else "main"
    val dirs: List[String] = root :: project :: "src" :: sets :: "scala" :: (
      meta.groupId.split("\\.").toList ++: (meta.artifact :: {
        if (entries.size == 1) Nil else entries.init
      }))

    val dir = dirs.reduceLeft((a, b) => a + File.separator + b)
    val outputFile = dir + File.separator + tdata.get("file")
    FileUtils.forceMkdir(new File(dir))

    val fsrc: Source[ByteString, NotUsed] = Source.fromFuture(process(template, tdata).map(ByteString(_)))
    val sink = FileIO.toPath(Paths.get(outputFile), Set(CREATE, WRITE))
    val runnable = fsrc.toMat(sink)(Keep.right)

    runnable.run().map(result => (tdata.get("file"), result))
  }

  def genAll(gc: GraphConfig)(implicit system: ActorSystem) = {
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
      m("XFlowApp.ftl", s"${gc.entry}App.scala", getAppCode(gc)) ::
        m("XFlowGraph.ftl", s"${gc.entry}Graph.scala", getGraphCode(gc)) ::
        m("utask.XFlowModels.ftl", s"${gc.entry}Models.scala", getModelsCode(gc)) ::
        m("XFlowGraph.ftl", s"${gc.entry}Config.scala", getConfigCode(gc)) ::
        m("config.XFlowPoints.ftl", s"${gc.entry}Points.scala", getPointsCode(gc)) ::
        m("utask.UTaskRoute.ftl", s"UTaskRoute.scala", getUTaskRouteCode(gc)) ::
        (ms("utask.XFlowTask.ftl", getUTaskCode(gc)) ++
          ms("utask.TaskTest.ftl", getUTaskTestCode(gc)))

    Future.traverse(all) { entry =>
      CodeEngine.genFile(entry._1, entry._2, "/tmp", s"aegis-zflow-${gc.artifact}")
    }.foreach(_.foreach {
      case (filename, result) =>
        println(s"${filename} => ${result.status}, ${result.count} bytes read.")
    })
  }

  def getAppCode(graphConfig: GraphConfig) = null

  def getGraphCode(graphConfig: GraphConfig) =
    CodeConfig(
      new JMap[String, String] {
        put("a", "a");
        put("b", "b")
      },
      new JMap[String, String] {
        put("V", "V");
        put("W", "W")
      },
      new JMap[String, Edge] {
        put("E1", Edge(name = "E1", end = "end"))
      },
      new JMap[String, TaskInfo] {
        put("U1", TaskInfo("U1", List("a", "b", "c")))
      },
      new JMap[String, TaskInfo] {
        put("A1", TaskInfo("A1", List("X", "Y", "Z")))
      }
    )

  def getModelsCode(graphConfig: GraphConfig) = (null)

  def getConfigCode(graphConfig: GraphConfig) = (getGraphCode(graphConfig))

  def getPointsCode(graphConfig: GraphConfig) = (
    CodePoints(new JMap[String, String] {
      put("a", "a");
      put("b", "b")
    })
    )

  def getUTaskCode(graphConfig: GraphConfig) = {
    List("TaskHello.scala" -> "Hello")
  }

  def getUTaskTestCode(graphConfig: GraphConfig) = {
    List("TaskHelloTest.scala" -> "Hello")
  }

  def getUTaskRouteCode(graphConfig: GraphConfig) = (Array("T1", "T2"))
}