package com.yimei.zflow.engine.graph

import akka.actor.ActorRef
import akka.http.scaladsl.server.Route
import com.yimei.zflow.api.models.flow._
import com.yimei.zflow.api.models.auto.CommandAutoTask

import scala.concurrent.Future

/**
  *
  */
trait FlowGraph {
  val flowInitial: String
  val timeout: Long
  val persistent: Boolean
  val points: Map[String, String]
  val edges: Map[String, Edge]
  val vertices: Map[String, String]
  val inEdges: Map[String, Array[String]] = Map()
  val flowType: String
  val userTasks: Map[String, TaskInfo]
  val autoTasks: Map[String, TaskInfo]
  val pointEdges: Map[String, String]

  val routes: Seq[ActorRef => Route] = Seq()

  val blueprint: Graph = Graph(edges, vertices, None, points, userTasks, autoTasks)

  def graph(state: State): Graph = Graph(edges, vertices, Some(state), points, userTasks, autoTasks)

  // todo 王琦优化!!!!
  protected def pointEdgesImpl: Map[String, String] = {
    def process(name: String, e: Edge) = {

      val userPointMap = e.userTasks.map { (ut: String) =>
        userTasks(ut).points.map(pt =>
          (pt -> name)
        ).toMap
      }.foldLeft(Map[String, String]())((acc, elem) => acc ++ elem)

      val autoPointMap = e.autoTasks.map { (ut: String) =>
        autoTasks(ut).points.map(pt =>
          (pt -> name)
        ).toMap
      }.foldLeft(Map[String, String]())((acc, elem) => acc ++ elem)

      val partUPointMap = (for {
        put <- e.partUTasks
        task <- put.tasks
        pt <- userTasks(task).points
      } yield (pt -> name)).toMap

      val partGPointMap = (for {
        put <- e.partGTasks
        task <- put.tasks
        pt <- userTasks(task).points
      } yield (pt -> name)).toMap

      autoPointMap ++ userPointMap ++ partUPointMap ++ partGPointMap
    }

    var ret: Map[String, String] = Map()
    for ((name, ed) <- edges) {
      ret = ret ++ process(name, ed)
    }

    ret
  }

  val autoMethods: Map[String, CommandAutoTask => Future[Map[String, String]]] = {
    this.getClass.getMethods.filter { m =>
      val ptypes = m.getParameterTypes
      ptypes.length == 1 &&
        ptypes(0) == classOf[CommandAutoTask] &&
        m.getReturnType == classOf[Future[Map[String, String]]]
    }.map { am =>

      val behavior: CommandAutoTask => Future[Map[String, String]] = task =>
        am.invoke(this, task).asInstanceOf[Future[Map[String, String]]]
      (am.getName -> behavior)
    }.toMap
  }

  val deciders: Map[String, State => Seq[Arrow]] = {
    this.getClass.getMethods.filter { m =>
      val ptypes = m.getParameterTypes
      ptypes.length == 1 &&
        ptypes(0) == classOf[State] &&
        m.getReturnType == classOf[Seq[Arrow]]
    }.map { am =>
      val behavior: State => Seq[Arrow] = (state: State) =>
        am.invoke(this, state).asInstanceOf[Seq[Arrow]]
      (am.getName -> behavior)
    }.toMap ++ Map("success" -> null, "fail" -> null)
  }

  val moduleJar: AnyRef = this
}




