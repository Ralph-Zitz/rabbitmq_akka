package io.scalac.rabbit

import akka.MasterActor

import scala.concurrent.Future
import scala.util.{Failure, Success}

import akka.actor.{Props, ActorSystem}
import akka.util.ByteString

import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl.{Source, Sink}

import com.typesafe.scalalogging.slf4j.LazyLogging

import io.scalac.amqp.{Connection, Message, Queue}

import io.scalac.rabbit.RabbitRegistry._


object ConsumerApp extends App with FlowFactory with LazyLogging {

  implicit val actorSystem = ActorSystem("rabbit-akka-stream")
  implicit val masterActor = actorSystem.actorOf(Props[MasterActor], "master")
  
  import actorSystem.dispatcher
  
  implicit val materializer = ActorFlowMaterializer()
  
  val connection = Connection()
  
  setupRabbit() onComplete { 
    case Success(_) =>
      logger.info("Exchanges, queues and bindings declared successfully.")
    
      val rabbitConsumer = Source(connection.consume(inboundQueue.name))
      val rabbitPublisher = Sink(connection.publish(outboundExchange.name))
      
      val flow = rabbitConsumer via consumerMapping via domainProcessing via publisherMapping to rabbitPublisher
    
      logger.info("Starting the flow")
      flow.run()
      
      logger.info("Setting up actors")
      initActors()
    case Failure(ex) =>
      logger.error("Failed to declare RabbitMQ infrastructure.", ex)
  }  
  
  def initActors(): Unit = {
    masterActor ! InitMsg("Setting up MasterActor")
  }

  def setupRabbit(): Future[List[Queue.BindOk]] =
    Future.sequence(List(
        
      /* declare and bind inbound exchange and queue */
      Future.sequence {
        connection.exchangeDeclare(inboundExchange) :: 
        connection.queueDeclare(inboundQueue) :: Nil
      } flatMap { _ =>
        Future.sequence {
	        connection.queueBind(inboundQueue.name, inboundExchange.name, "") :: Nil
        }
      },

      /* declare and bind outbound exchange and queues */
      Future.sequence {
        connection.exchangeDeclare(outboundExchange) :: 
        connection.queueDeclare(outOkQueue) ::
        connection.queueDeclare(outNokQueue) :: Nil
      } flatMap { _ =>
        Future.sequence {
          connection.queueBind(outOkQueue.name, outboundExchange.name, outOkQueue.name) ::
	        connection.queueBind(outNokQueue.name, outboundExchange.name, outNokQueue.name) :: Nil
        }
      }
    )).map { _.flatten }
}
