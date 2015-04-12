package akka

import _root_.io.scalac.rabbit.InitMsg
import akka.actor.Actor
import com.typesafe.scalalogging.slf4j.LazyLogging

class ConsumerActor extends Actor with LazyLogging {
  def receive: Receive = {
    case InitMsg(m) => logger.info(m)
  }
}
