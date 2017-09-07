package com.onecreation.perfecttransit

import akka.actor.FSM.Normal
import akka.actor.{Actor, FSM, Props}

import scala.concurrent.duration._

sealed trait VehicleState

case object NotInService extends VehicleState

case object InService extends VehicleState

case object AtStop extends VehicleState


sealed trait VehicleData

case object NoRouteAssigned extends VehicleData

sealed trait VehicleMessage

case object InfoRequest extends VehicleMessage

case object TransitToNextStop extends VehicleMessage
case object Decommission extends VehicleMessage
case object Decommissioned extends VehicleMessage
case object VehicleDispatched extends VehicleMessage

case class RouteAssigned(name: String, direction: String) extends VehicleMessage

case class ArrivedAtStop(stop: RouteStop, onRoute: Route) extends VehicleMessage


case class Route(name: String, direction: String) extends VehicleData

case class RouteStop(name: String, onRoute:Route) extends VehicleData

class Vehicle extends FSM[VehicleState, VehicleData] {

  startWith(NotInService, NoRouteAssigned)

  when(NotInService) {
    case Event(InfoRequest, _) => stay replying NoRouteAssigned
    case Event(route: RouteAssigned, stateData) => {
      goto(InService) using Route(route.name, route.direction) replying Route(route.name, route.direction)
    }
  }

  when(InService) {
    case Event(InfoRequest, currentRoute: Route) => stay using currentRoute replying currentRoute
    case Event(arrival: RouteStop, _) => {
      val stop = arrival.copy()
      goto(AtStop) using stop replying stop
    }
  }

  when(AtStop, stateTimeout = 10 seconds) {
    case Event((TransitToNextStop | StateTimeout), s: RouteStop) => goto(InService) using s.onRoute
    case Event(Decommission, _) => stop(Normal) replying(Decommissioned)
    case Event(_, _) => stay
  }

  onTransition {
    case NotInService -> InService => {
    }
  }

  onTermination{
    case StopEvent(FSM.Normal, state, data) => log.info(s"Shutting down vehicle with $state and $data")
  }


  initialize()
}

object Vehicle {
  def props(): Props = Props[Vehicle]
}
