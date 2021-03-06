package com.yimei.zflow.engine.graph

import java.io.{File, InputStream}

import akka.actor.ActorRef
import akka.http.scaladsl.server.Route
import com.yimei.zflow.api.models.auto.CommandAutoTask
import com.yimei.zflow.api.models.flow._
import com.yimei.zflow.engine.FlowRegistry

import scala.concurrent.Future
import scala.io.Source


/**
  * Created by hary on 16/12/17.
  */
object GraphLoader extends FlowProtocol {

  def deploy(flowType: String) = {
    FlowRegistry.register(flowType, loadGraph(flowType, getClassLoader(flowType)))
  }

  def getClassLoader(flowType: String) = {
    flowType match {
      case "money" => this.getClass.getClassLoader
      //      case "zhou" =>
      //        // todo xj
      //        // 改为从数据库deploy表里读取jar文件 写入/tmp/$flowType.jar
      //        // 然后再  new java.net.URLClassLoader(new File("/tmp/xxx.jar").toURI.toURL), this.getClass.getClassLoader)
      //        null

      case _ => val jars: Array[String] = (new File("flows/" + flowType))
        .listFiles()
        .filter(_.isFile())
        .map(_.getPath)
        new java.net.URLClassLoader(jars.map(new File(_).toURI.toURL), this.getClass.getClassLoader)

    }
  }

  // todo 王琦
  // 改为从数据加载
  def loadFromDB() = {
    // select * from deploy where enabled = true
    // 对每个entry,   取出jar内容, 放入/tmp目录下, 然后拿到classLoader
    // graph = loadGraph(flowType, classLoader)
    // FlowRegistry.register(flowType, graph)
  }

  // 从目录加载
  def loadall() =
    new File("flows")
      .listFiles()
      .filter(_.isDirectory())
      .map { file =>
        val flowType = file.getName
        val classLoader = getClassLoader(flowType)

        val status = try {
          FlowRegistry.register(flowType, loadGraph(flowType, classLoader))
        } catch {
          case e: Throwable =>
            println(flowType + "!!!!!!! error:" + e.getMessage)

            false
        }
        (flowType, status)

      }.toMap


  def loadConfig(classLoader: ClassLoader): GraphConfig = {
    loadConfig(classLoader.getResourceAsStream("flow.json"))
  }

  def loadConfig(inputStream: InputStream) = {

    import spray.json._
    var graphConfig = Source.fromInputStream(inputStream)
      .mkString
      .parseJson
      .convertTo[GraphConfig]

    // todo need add start success and fail ?
    graphConfig = graphConfig.copy(edges = graphConfig.edges ++
      Map("start" -> Edge(name = "start", begin = "God", end = graphConfig.globalConfig.initial),
        "success" -> Edge(name = "success", end = "success"),
        "fail" -> Edge(name = "fail", end = "success")
      )
    )

    // 哪些vertex有next属性
    val vnext = graphConfig.edges.values.groupBy(_.begin).filter{
      case (begin, edges) => edges.size == 1
    }.map {
      case (begin, edges) => (begin, Some(Arrow(edges.head.end, Some(edges.head.name))))
    }

    // 调整
    val vajust = graphConfig.vertices.map { v =>
      if (vnext.contains(v._1)) {
        (v._1, Vertex(v._2.description, vnext(v._1)))
      } else {
        v
      }
    }
    graphConfig = graphConfig.copy(vertices = vajust)

    graphConfig
  }

  def loadGraph(gFlowType: String, classLoader: ClassLoader): FlowGraph = {

    val graphConfig = loadConfig(classLoader)

    val global = graphConfig.globalConfig

    // graphJar class and graphJar object
    val mclass = classLoader.loadClass(s"${global.groupId}.${global.artifact}.${global.entry}" + "Graph$")
    val graphJar = mclass.getField("MODULE$").get(null)


    val allDeciders = getDeciders(mclass, graphJar);
    val allAutos = getAutoMap(mclass, graphJar);

    // graph intial vertex
    val initial = global.initial

    val jarRoute = getRoutes(mclass, graphJar)

    // 返回流程
    val g = new FlowGraph {

      override val timeout: Long = global.timeout

      override val persistent: Boolean = global.persistent

      override val points: Map[String, String] = graphConfig.points

      override val vertices: Map[String, Vertex] =  graphConfig.vertices
//      {
//        graphConfig.vertices.map { entry =>
//          (entry._1, entry._2.description)
//        }
//      }

      override def graph(state: State): Graph = Graph(
        graphConfig.edges,
        // graphConfig.vertices.map { entry => (entry._1, entry._2.description) },
        graphConfig.vertices,
        Some(state),
        graphConfig.points,
        graphConfig.userTasks,
        graphConfig.autoTasks
      )

      override val blueprint: Graph = Graph(
        graphConfig.edges,
        // graphConfig.vertices.map { entry => (entry._1, entry._2.description) },
        graphConfig.vertices,
        None,
        graphConfig.points,
        graphConfig.userTasks,
        graphConfig.autoTasks
      )

      override val inEdges: Map[String, Array[String]] = graphConfig.edges.groupBy { entry =>
        entry._2.end
      }.map { e =>
        (e._1, e._2.keySet.toArray)
      }


      override val flowInitial: String = initial

      override val flowType: String = gFlowType

      override val userTasks: Map[String, TaskInfo] = graphConfig.userTasks

      override val autoTasks: Map[String, TaskInfo] = graphConfig.autoTasks

      override val edges: Map[String, Edge] = graphConfig.edges

      override val pointEdges = pointEdgesImpl

      override val autoMethods: Map[String, CommandAutoTask => Future[Map[String, String]]] = allAutos

      override val deciders: Map[String, State => Seq[Arrow]] = allDeciders

      override val moduleJar: AnyRef = graphJar

      override val route = jarRoute
    }

    g
  }

  def getAutoMap(m: Class[_], module: AnyRef) = {
    m.getMethods.filter { method =>
      val ptypes = method.getParameterTypes
      ptypes.length == 1 &&
        ptypes(0) == classOf[CommandAutoTask] &&
        method.getReturnType == classOf[Future[Map[String, String]]]
    }.map { am =>
      val behavior: CommandAutoTask => Future[Map[String, String]] =
        task => am.invoke(module, task).asInstanceOf[Future[Map[String, String]]]
      (am.getName -> behavior)
    }.toMap
  }

  def getDeciders(m: Class[_], module: AnyRef) = {
    m.getMethods.filter { method =>
      val ptypes = method.getParameterTypes
      ptypes.length == 1 &&
        ptypes(0) == classOf[State] &&
        method.getReturnType == classOf[Seq[Arrow]]
    }.map { am =>

      val behavior: State => Seq[Arrow] = (state: State) =>
        am.invoke(module, state).asInstanceOf[Seq[Arrow]]
      (am.getName -> behavior)

    }.toMap
  }

  def getRoutes(m: Class[_], module: AnyRef): () => Route = {
    m.getMethods.filter { method =>
      method.getName == "moduleRoute" &&
        method.getReturnType == classOf[Route]
    }.map { am =>
      () => am.invoke(module).asInstanceOf[Route]
    }.head
  }
}
