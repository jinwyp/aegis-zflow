package com.yimei.zflow.engine.admin

/**
  * Created by hary on 17/1/10.
  */
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.yimei.zflow.util.config.Core
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.FileTemplateResolver

import scala.concurrent.Future

/**
  * Created by hary on 17/1/5.
  */
trait ThymeleafConfig extends Core {


  val templateResolver = new FileTemplateResolver();
  printf("-------------------------------------------------------------------------")
  printf(coreConfig.getString("thymeleaf.path"))
  printf("-------------------------------------------------------------------------")
  templateResolver.setPrefix(coreConfig.getString("thymeleaf.path") )
  templateResolver.setSuffix(".html");
  templateResolver.setTemplateMode(TemplateMode.HTML);
  templateResolver.setCacheTTLMs(3600000L);
  templateResolver.setCacheable(false);

  val templateEngine = new TemplateEngine();
  templateEngine.setTemplateResolver(templateResolver);

  def th(template: String, context: Context) = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val src = Source.fromFuture(Future {
      ByteString(templateEngine.process(template, context))
    })
    complete(HttpResponse(status = StatusCodes.OK, entity = HttpEntity(`text/html(UTF-8)`, src)))
  }

  def context: Context = new Context()
}

