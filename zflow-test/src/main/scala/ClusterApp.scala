import akka.actor.ActorSystem

/**
  * Created by hary on 17/1/10.
  */
object ClusterApp extends App {

  val system = ActorSystem("ClusterSystem")

  println("hello world")

}
