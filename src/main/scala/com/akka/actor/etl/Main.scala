package com.akka.actor.etl

import akka.actor.{ActorRef, ActorSystem, Props}

import com.gettyimages.dsa.rmq.{RmqConfig, RmqConnection, RmqManager, RmqPublisher}

object Main extends App {
  println("Initializing rabbitmq...")
  val system = ActorSystem("rmq-relay")
  try {
    val rmqConn: ActorRef = RmqManager.createConnectionActor(system, RmqConfig.getEndpoint(EtlConfig.RabbitMQ.virtualHost))
    val rmqPublisher: RmqPublisher = RmqConnection.createPublisher(EtlConfig.RabbitMQ.publishEndpoint)
    val publisherProps: Props = EtlRmqPublisher.props(rmqPublisher).withDispatcher("publisher-dispatcher")
    val publisherActor: ActorRef = system.actorOf(publisherProps, "persistencePublisher")

    val consumerDispatcher = "consumer-dispatcher"
    system.actorOf(RmqConsumer.props(rmqConn, publisherActor).withDispatcher(consumerDispatcher))

  } catch {
    case t: Throwable ⇒ {
//      log.error("Failed to initialize rabbitmq", Map("exception" -> t.getMessage))
      throw t
    }
    }

}
