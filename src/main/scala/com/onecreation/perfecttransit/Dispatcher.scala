package com.onecreation.perfecttransit


import akka.actor.{Actor, ActorLogging, Props}
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

import scala.concurrent.Future


class Dispatcher extends Actor with ActorLogging {
  implicit val materializer = ActorMaterializer()
  val subscription = Consumer
    .committableSource(
      ConsumerSettings(context.system, new ByteArrayDeserializer, new StringDeserializer)
        .withGroupId("dispatch")
        .withBootstrapServers("localhost:9092"),
      Subscriptions.topics("dispatch-request"))
    .mapAsync[String](1)(cm => {
      log.info(cm.record.value())
      Future.successful{cm.record.value() }
    }).runWith(Sink.actorRef(context.self, None))

  override def receive = {
    case "dispatch-request" => log.info("Vehicle Dispatched")
  }

}

object Dispatcher {

  final case class DispatchVehicle(name: String, routeName: String, direction: String)

  val props: Props = Props[Dispatcher]
}
