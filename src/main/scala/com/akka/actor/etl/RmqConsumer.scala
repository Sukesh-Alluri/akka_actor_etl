package com.akka.actor.etl

import akka.actor.{ActorRef, Props}

import com.gettyimages.dsa.logging.DsaLogging
import com.gettyimages.dsa.persistence.consumer.MessageConsumer
import com.gettyimages.dsa.persistence.consumer.MessageConsumer.Acknowledge
import com.gettyimages.dsa.rmq.{PublishMessage, DsaDelivery}

object RmqConsumer extends DsaLogging {
  val queueName = "persistence-relay"

  val routingKeys =
    Seq(
      "v1.management.assetcreate.#",
      "v1.management.assetupdate.#"
    )

  def props(rmqConn: ActorRef, assetPublisher: ActorRef) =
    Props(classOf[AssetConsumer], rmqConn, assetPublisher, routingKeys, queueName)

}

class AssetConsumer(rmqConn: ActorRef, assetPublisher: ActorRef,
                    routingKeys: Seq[String], queueName: String)
  extends MessageConsumer {

  override def preStart(): Unit = {
    createConsumerAndDLQ(rmqConn, queueName, routingKeys, (d: DsaDelivery) => d)
  }

  override def receive: Receive = messageConsumerReceive orElse defaultConsumerReceive

  def messageConsumerReceive: Receive = {
    case delivery: DsaDelivery =>
      rmqSender = sender
      log.info("Message consumed", Map(
        "consumer" -> "RmqConsumer",
        "masterId" -> delivery.headers.getOrElse("MasterId", ""),
        "correlationId" -> delivery.correlationId,
        "routingKey" -> delivery.routingKey,
        "messageId" -> delivery.messageId)
      )
      assetPublisher ! delivery
    case (msg: PublishMessage, deliveryTag: Long) =>
      self ! Acknowledge(deliveryTag)
  }
}
