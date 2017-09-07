package com.onecreation.perfecttransit



import akka.actor.{Actor, ActorLogging, Props}
import akka.stream._
import akka.stream.alpakka.amqp.IncomingMessage
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import play.api.libs.json._

import scala.concurrent.Future


sealed trait Message

case class DispatchMessage(messageId: String, messageType: String) extends Message

case object InvalidDispatchMessage extends Message

class Dispatcher extends Actor
  with ActorLogging
  with IncomingMessageParser {
  implicit val materializer = ActorMaterializer()
  implicit val executor = context.dispatcher
  val amqpUri = "localhost"
  val amqpPort = 5672
  val incomingStream = DispatchStream.getSource(amqpUri, amqpPort, "dispatch")
  val outgoingStream = DispatchStream.getSink(amqpUri, amqpPort, "dispatch")
  val messagingQueue = Source.queue[ByteString](10, OverflowStrategy.dropNew).to(outgoingStream).run()
  implicit val dispatchMessageReads = Json.reads[DispatchMessage]

  def convertIncomingMessage = { (message: IncomingMessage) =>
    Future {
      Json.fromJson[DispatchMessage](Json.parse(message.bytes.utf8String)) match {
        case JsSuccess(d, path) => d
        case e: JsError => DispatchMessage("None", "None")
      }
    }


  }


  override def preStart(): Unit = {
    incomingStream.mapAsync(parallelism = 1)(convertIncomingMessage).runWith(Sink.actorRef(context.self, "Done"))
  }

  override def receive = {
    case dispatchMessage: DispatchMessage => {
      log.info(s"Vehicle Dispatched: $dispatchMessage")
      context.actorOf(Vehicle.props()) ! InfoRequest
    }
    case NoRouteAssigned => {
      log.info(s"Reply from sender: ${sender()}")
      messagingQueue offer ByteString("Testy Test")
    }
    case InvalidDispatchMessage => log.error("failed to dispatch vehicle from request")
    case _ => log.info(s"Testy test")
  }

}

object Dispatcher {

  final case class DispatchVehicle(name: String, routeName: String, direction: String)

  val props: Props = Props[Dispatcher]
}
