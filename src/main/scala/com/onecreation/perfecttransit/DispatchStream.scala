package com.onecreation.perfecttransit

import akka.{Done, NotUsed}
import akka.stream.alpakka.amqp
import akka.stream.alpakka.amqp.{AmqpConnectionDetails, AmqpSinkSettings, IncomingMessage, TemporaryQueueSourceSettings}
import akka.stream.alpakka.amqp.scaladsl.{AmqpSink, AmqpSource}
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString

import scala.concurrent.Future


object DispatchStream {

  def getSource(host: String, port: Int, exchangeName: String): Source[IncomingMessage, NotUsed] = AmqpSource(
    TemporaryQueueSourceSettings(
      AmqpConnectionDetails(amqp.Seq((host, port))), exchangeName,routingKey = Some("create")), 10)

  def getSink(host: String, port: Int, exchangeName: String): Sink[ByteString, Future[Done]] = AmqpSink.simple(AmqpSinkSettings(AmqpConnectionDetails(amqp.Seq((host, port))),Some(exchangeName)))
}
