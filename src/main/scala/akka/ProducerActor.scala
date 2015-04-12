package akka

import _root_.io.scalac.amqp.Message
import _root_.io.scalac.rabbit.ConsumerApp._
import _root_.io.scalac.rabbit.InitMsg
import _root_.io.scalac.rabbit.RabbitRegistry._
import akka.actor.Actor
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import com.typesafe.scalalogging.slf4j.LazyLogging

import scala.util.{Failure, Success}

class ProducerActor extends Actor with LazyLogging {

  def receive: Receive = {
    case InitMsg(m) => logger.info(m)
      logger.info("Starting the trial run")
      trialRun()
  }

  def trialRun() = {
    val trialMessages = "message 1" :: "message 2" :: "message 3" :: "message 4" :: "message 5" :: Nil

    /* publish couple of trial messages to the inbound exchange */
    Source(trialMessages).
      map(msg => Message(ByteString(msg))).
      runWith(Sink(connection.publish(inboundExchange.name, "")))

    /* log the trial messages consumed from the queue */
    Source(connection.consume(outOkQueue.name)).
      take(trialMessages.size).
      map(msg => logger.info(s"'${msg.message.body.utf8String}' delivered to ${outOkQueue.name}")).
      runWith(Sink.onComplete({
      case Success(_) => logger.info("Trial run finished. You can now go to http://localhost:15672/ and try publishing messages manually.")
      case Failure(ex) => logger.error("Trial run finished with error.", ex)
    }))
  }
}
