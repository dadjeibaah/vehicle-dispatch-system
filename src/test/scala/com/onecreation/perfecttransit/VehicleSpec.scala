package com.onecreation.perfecttransit

import java.util.UUID

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestFSMRef, TestKit}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpecLike, Matchers}

class VehicleSpec(_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with ImplicitSender
    with FlatSpecLike
    with BeforeAndAfterEach
    with BeforeAndAfterAll {

  def this() = this(ActorSystem("TestDispatcher"))

  def fsm = TestFSMRef(new Vehicle(), UUID.randomUUID().toString)

  override def beforeEach(): Unit = {

  }

  override def afterEach(): Unit = {

  }

  override def afterAll(): Unit = {
    shutdown(system)
  }

  "A vehicle" should "reply with a no route assigned message and should not be in service" in {
    val stateMachine = fsm
    stateMachine ! InfoRequest
    expectMsg(NoRouteAssigned)
    stateMachine.stop()
  }

  it should "transition to InService when a route is assigned" in {
    val stateMachine = fsm
    stateMachine ! RouteAssigned("J Line", "Inbound")
    stateMachine.stop()
    expectMsg(Route("J Line", "Inbound"))
    stateMachine.stop()
  }

  it should "reply with the current assigned route upon info request" in {
    val stateMachine = fsm
    val route = Route("L Line", "Inbound")
    stateMachine ! RouteAssigned(route.name, route.direction)
    stateMachine ! InfoRequest
    expectMsg(route)
    stateMachine.stop()
  }

  it should "notify once it gets at its stop" in {
    val stateMachine = fsm
    val route = Route("L Line", "Inbound")
    val routeStop = RouteStop("Station1", route)
    stateMachine ! RouteAssigned(route.name, route.direction)
    stateMachine ! routeStop
    stateMachine.stateName shouldBe AtStop
    stateMachine.stateData shouldBe routeStop
    stateMachine.stop()
  }

  it should "be decommissioned once it receives the decommission event from its dispatcher" in {
    val stateMachine = fsm
    val route = Route("L Line", "Inbound")
    val routeStop = RouteStop("Station1", route)
    stateMachine ! RouteAssigned(route.name, route.direction)
    stateMachine ! routeStop
    stateMachine.stateName shouldBe AtStop
    stateMachine ! Decommission
    fsm.stateName shouldBe NotInService
    fsm.stateData shouldBe NoRouteAssigned

  }
}
