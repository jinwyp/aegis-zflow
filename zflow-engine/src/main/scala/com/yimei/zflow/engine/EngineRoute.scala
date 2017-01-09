package com.yimei.zflow.engine

import com.yimei.zflow.engine.deploy.DeployRoute
import com.yimei.zflow.engine.editor.EditorRoute
import akka.http.scaladsl.server.Directives._
import com.yimei.zflow.engine.auto.AutoRoute
import com.yimei.zflow.engine.flow.FlowRoute
import com.yimei.zflow.engine.gtask.GTaskRoute
import com.yimei.zflow.engine.utask.UTaskRoute
import com.yimei.zflow.engine.util.organ.routes.UserRoute

/**
  * Created by wangqi on 17/1/4.
  */
trait EngineRoute extends EditorRoute with DeployRoute with FlowRoute with GTaskRoute with UTaskRoute with AutoRoute {
  def engineRoute = editorRoute ~ deployRoute ~ gtaskRoute ~ utaskRoute ~ autoRoute ~ flowRoute
}

