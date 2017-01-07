package com.yimei.zflow.util.module

import akka.actor.{Actor, ActorLogging, ActorRef, ReceiveTimeout, SupervisorStrategy, Terminated}

import scala.concurrent.duration._

object ModuleMaster {
  case class RegisterModule(name: String, actor: ActorRef)

  case class GiveMeModule(name: String)

  case class UnderIdentify()
}

/**
  * Created by hary on 16/12/3.
  */
/**
  *
  * @param moduleName 本模块的名称
  * @param dependOn   依赖哪些模块
  * @param askWho     找谁协商
  */
abstract class ModuleMaster(moduleName: String, dependOn: Array[String], askWho: Option[ActorRef] = None)
  extends Actor
    with ServicableBehavior
    with ActorLogging {

  import ModuleMaster._

  override def supervisorStrategy: SupervisorStrategy = super.supervisorStrategy

  // 相关联的模块
  var modules: Map[String, ActorRef] = Map(moduleName -> self) // 自己也在里面

  private val who = askWho.getOrElse(context.parent)

  // 请求父亲告知其他模块
  dependOn.foreach { name =>
    log.debug(s"${moduleName} 请求获取 ${name}")
    who ! GiveMeModule(name)
  }

  context.setReceiveTimeout(50 millis)

  def ignoring: Receive = {
    case _: RegisterModule =>
  }

  override def receive = identify

  /**
    * after module-negotiation, the master need more initialization before going into serving state
    */
  def initHook(): Unit = {
    log.debug("initHook is void")
  }

  def identify: Receive = {

    // 拿到模块
    case RegisterModule(name, ref) =>
      modules = modules + (name -> ref)
      log.debug(s"get module $name")
      context.watch(ref)

      if (ready) {
        initHook()
        context.become(serving orElse ignoring)
        log.info(s"${moduleName} is servicable now dependOn = ${modules.keys}")
        context.setReceiveTimeout(Duration.Undefined)
      } else {
        context.setReceiveTimeout(50 millis)
      }

    // 没有收到, 看还有那些模块没有拿到, 就重新请求parent
    case ReceiveTimeout =>
      dependOn.filter(!modules.contains(_)).foreach(who ! GiveMeModule(_))
      context.setReceiveTimeout(50 millis)

    // identifying阶段, 不能处理消息
    case msg =>
      log.error(s"${moduleName} are not prepared")
      sender() ! UnderIdentify
  }

  /**
    * check whether this module is ready for service
    *
    * @return
    */
  def ready() = dependOn.find(!modules.contains(_)).fold(true)(_ => false)

  override def unhandled(message: Any): Unit = {
    message match {
      // dependent module died
      case Terminated(ref) =>
        log.error(s"$moduleName restart because of ${ref.path.name} died")
        context.setReceiveTimeout(50 millis)
        context.become(identify) // re-enter the identify
        context.children.foreach(context.stop(_)) // stop all child

        // 重新获取依赖的模块
        modules.find(entry => entry._2 == ref).foreach(t =>
          who ! GiveMeModule(t._1)
        )

      case _ =>
        log.error(s"can not process $message")
        super.unhandled(message)
    }
  }

}






