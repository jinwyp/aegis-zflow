package com.yimei.zflow.engine

import akka.http.scaladsl.server.Directives._
import com.yimei.zflow.engine.auto.AutoRoute
import com.yimei.zflow.engine.deploy.DeployRoute
import com.yimei.zflow.engine.editor.EditorRoute
import com.yimei.zflow.engine.flow.FlowRoute
import com.yimei.zflow.engine.gtask.GTaskRoute
import com.yimei.zflow.engine.utask.UTaskRoute

/**
  * Created by wangqi on 17/1/4.
  */
trait EngineRoute extends EditorRoute
  with DeployRoute
  with FlowRoute
  with GTaskRoute
  with UTaskRoute
  with AutoRoute {

  def engineRoute = gtaskRoute ~
    utaskRoute ~
    autoRoute ~
    flowRoute ~
    editorRoute ~
    deployRoute
}

