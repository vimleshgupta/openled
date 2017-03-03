package openled.resource

import javax.ws.rs.core.Response
import javax.ws.rs.core.Response._
import javax.ws.rs.{GET, Path}

import openled.service.{ElectricityServie, PushPinService}

/**
  * Created by cts1 on 25/2/17.
  */

@Path("/electricity")
class ElectricityResource {


  @GET
  @Path("/distance")
  def setup(): Unit = {
    ElectricityServie.init()
    ElectricityServie.trigger()

  }

  @GET
  @Path("/startSensor")
  def startSensor(): Unit = {
    ElectricityServie.init()

    ElectricityServie.startSensor()
  }


  @GET
  @Path("/on")
  def on(): Unit = {
    ElectricityServie.init()

    ElectricityServie.on()
  }

  @GET
  @Path("/off")
  def off(): Unit = {
    ElectricityServie.init()

    ElectricityServie.off()
  }


  @GET
  @Path("/shutdown")
  def shutdown(): Response = {
    ElectricityServie.cleanup()
    noContent().build()
  }
}
