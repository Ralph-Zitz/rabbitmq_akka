package akka

import _root_.io.scalac.rabbit.InitMsg
import akka.actor.{Props, Actor}
import akka.routing.RoundRobinPool
import com.typesafe.scalalogging.slf4j.LazyLogging

class MasterActor extends Actor with LazyLogging {

  val consumer = context.actorOf(Props[ConsumerActor].withRouter(RoundRobinPool(nrOfInstances = 5)), name = "consumer")
  val producer = context.actorOf(Props[ProducerActor].withRouter(RoundRobinPool(nrOfInstances = 5)), name = "producer")

  def receive: Receive = {
    case InitMsg(m) =>
      logger.info(m)
      consumer ! InitMsg("Setting up ConsumerActor")
      producer ! InitMsg("Setting up ProducerActor")
  }
}
