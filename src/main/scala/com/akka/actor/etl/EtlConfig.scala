package com.akka.actor.etl

import com.typesafe.config.{ConfigFactory, Config}


object EtlConfig {

  val config: Config = ConfigFactory.load

  object RabbitMQ {
    lazy val virtualHost = config.getString("dsa.rmq.virtualHost")
    lazy val deadLetterExchange = config.getString("dsa.rmq.deadLetterExchange")

    lazy val publishHost = config.getString("dsa.rmq.publishHost")
    lazy val publishEndpoint = s"amqp://dev:dev@$publishHost:5672/dsa"
  }

}