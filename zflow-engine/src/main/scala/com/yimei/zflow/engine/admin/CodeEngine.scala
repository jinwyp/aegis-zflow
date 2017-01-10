package com.yimei.zflow.engine.admin

import java.io.{ByteArrayOutputStream, OutputStreamWriter}

import freemarker.template.{Configuration, TemplateExceptionHandler}

import scala.concurrent.Future

/**
  * Created by hary on 17/1/10.
  */
object CodeEngine {

  val ftlConfig = new Configuration(Configuration.VERSION_2_3_23);
  ftlConfig.setClassLoaderForTemplateLoading(this.getClass.getClassLoader, "code")
  ftlConfig.setDefaultEncoding("UTF-8");
  ftlConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
  ftlConfig.setLogTemplateExceptions(false);

  def process(template: String, tdata: AnyRef): Future[String] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    Future {
      val os = new ByteArrayOutputStream()
      val out = new OutputStreamWriter(os)
      ftlConfig.getTemplate(template).process(tdata, out)
      os.toString
    }
  }
}
