package com.onecreation.perfecttransit

import akka.stream.alpakka.amqp.IncomingMessage

import scala.concurrent.Future

trait IncomingMessageParser {
  def convertIncomingMessage:(IncomingMessage => Future[Message])
}
