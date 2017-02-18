package openled.resource

import javax.ws.rs.core.Response
import javax.ws.rs.core.Response._
import javax.ws.rs.{GET, Path}

import openled.service.PushPinService

/**
  * Created by cts1 on 17/2/17.
  */
@Path("/pushpin")
class PushPinResource {

  @GET
  @Path("/setup")
  def setup(): Response = {
    PushPinService.setup()
    noContent().build()
  }

  @GET
  @Path("/shutdown")
  def shutdown(): Response = {
    PushPinService.cleanup()
    noContent().build()
  }

}
