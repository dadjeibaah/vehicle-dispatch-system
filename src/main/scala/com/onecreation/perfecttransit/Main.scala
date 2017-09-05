package com.onecreation.perfecttransit

import akka.actor.ActorSystem
import com.onecreation.perfecttransit.Dispatcher.DispatchVehicle

object Main extends App {
  val system: ActorSystem = ActorSystem("perfecttransit")
  val dispatcherRef = system.actorOf(Dispatcher.props, "dispatcher")
  dispatcherRef ! DispatchVehicle("123B","J Line", "Inbound")
  val vehRef = system.actorOf(Vehicle.props)
}
