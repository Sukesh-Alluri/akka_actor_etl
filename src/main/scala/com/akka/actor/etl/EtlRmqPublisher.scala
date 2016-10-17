package com.akka.actor.etl

import scala.concurrent.duration._

import akka.actor._
import akka.pattern.pipe
import atmos.dsl.Slf4jSupport._
import atmos.dsl._
import org.json4s._
import org.json4s.native.JsonMethods._

import com.gettyimages.dsa.logging._
import com.gettyimages.dsa.rmq._

object EtlRmqPublisher {

  def props(publisher: RmqPublisher) = Props(classOf[EtlRmqPublisher], publisher)

}

class EtlRmqPublisher(publisher: RmqPublisher) extends Actor with DsaLogging {

  import context.dispatcher

  implicit val republishPolicy = retryForever using exponentialBackoff {
    5 seconds
  } monitorWith {
    log.logger.underlying onRetrying logWarning onInterrupted logWarning onAborted logError
  }

  def receive: Receive = {
    case delivery: DsaDelivery =>
      republishPolicy.retryAsync() {
        publish(delivery)
      } pipeTo sender
  }

  def publish(delivery: DsaDelivery) = {
    publisher.publish(parse(delivery.body), delivery.correlationId, delivery.routingKey, delivery.headers, publishDurabilityPolicies = PublishDurabilityPolicies(confirm = true, mandatory = true)) map(msg => (msg, delivery.deliveryTag))
  }

  override def unhandled(msg: Any): Unit = msg match {
    case x =>
      log.warn(s"Unhandled message in Publisher $x.")
      super.unhandled(msg)
  }
}