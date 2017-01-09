package com.yimei.zflow.engine

import com.yimei.zflow.engine.deploy.DeployRoute
import com.yimei.zflow.engine.editor.EditorRoute
import akka.http.scaladsl.server.Directives._

/**
  * Created by wangqi on 17/1/4.
  */
trait EngineRoute extends EditorRoute with DeployRoute {
  def engineRoute = editorRoute ~ deployRoute
}

