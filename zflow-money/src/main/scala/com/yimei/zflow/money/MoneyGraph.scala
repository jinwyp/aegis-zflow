package com.yimei.zflow.money

import com.yimei.zflow.api.models.auto.CommandAutoTask
import com.yimei.zflow.api.models.flow.{Arrow, _}
import MoneyConfig._

import scala.concurrent.Future

/**
  * Created by wangqi on 16/12/23.
  */
object MoneyGraph {

  import scala.concurrent.ExecutionContext.Implicits.global

  val ArrowFail = Arrow("fail", None)
  val ArrowSuccess = Arrow("success", None)

  def V0(state: State) = { Seq(Arrow(vertex_V1, Some(edge_E1))) }
  def V1(state: State) = {
    if (state.points(point_SuccessRate).value.toDouble < 0.5) {
      Seq(ArrowFail)
    } else {
      Seq(Arrow(vertex_V4, Some(edge_E2)), Arrow(vertex_V3, Some(edge_E3)))
    }
  }
  def V3(state: State) = { Seq(Arrow(vertex_V4, Some(edge_E4))) }
  def V4(state: State) = { Seq(Arrow(vertex_V5, Some(edge_E3))) }
  def V5(state: State) = { Seq(Arrow(vertex_V6, Some(edge_E6))) }
  def V6(state: State) = {
    if (state.points(point_Approve).value == "yes") {
      Seq(ArrowSuccess)
    } else {
      Seq(Arrow(vertex_V4, Some(edge_E7)))
    }
  }

  def Divination(cmd: CommandAutoTask): Future[Map[String, String]] = Future {
    val rate: Double = ((new util.Random).nextInt(10)) / 10.0
    Map(point_SuccessRate -> rate.toString)
  }

}
