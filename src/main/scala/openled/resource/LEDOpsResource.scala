package openled.resource

import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.noContent
import javax.ws.rs.{GET, Path}

import com.pi4j.wiringpi.SoftPwm
import openled.service.LEDOpsService

/**
  * Created by cts1 on 9/2/17.
  */
@Path("/led")
class LEDOpsResource {

  val pin = LEDOpsService.gpioLEDPin()

  @GET
  @Path("/on")
  def on() : Response = {

    pin.high()
    println("light is: ON")
    noContent().build()
  }

  @GET
  @Path("/off")
  def off() : Response = {

    pin.low()
    println("light is: OFF")

    noContent().build()
  }

  @GET
  @Path("/blink")
  def blink() : Response = {

    pin.blink(500, 5000)
    println("light is: blink")
    noContent().build()
  }

  @GET
  @Path("/pulse")
  def pulse() : Response = {

    pin.pulse(2000)
    println("light is: pulse")
    noContent().build()
  }

  @GET
  @Path("/wakeupAndDim")
  def wakeupAndDim() : Response = {


    val PIN_NUMBER = 1
    //    Gpio.wiringPiSetup()
    // softPwmCreate(int pin, int value, int range)
    SoftPwm.softPwmCreate(PIN_NUMBER, 0, 100)

    var counter = 0
    while (counter < 3) {
      // fade LED to fully ON
      for (i <- 1 to 100) {
        // softPwmWrite(int pin, int value)
        // This updates the PWM value on the given pin. The value is
        // checked to be in-range and pins
        // that haven't previously been initialized via softPwmCreate
        // will be silently ignored.
        SoftPwm.softPwmWrite(PIN_NUMBER, i)
        Thread.sleep(25)
      }
      // fade LED to fully OFF
      for (i <- 100 to 1 by -1) {
        SoftPwm.softPwmWrite(PIN_NUMBER, i)
        Thread.sleep(25)
      }
      counter+=1
    }

    SoftPwm.softPwmStop(PIN_NUMBER)
    println("light is: wakeupAndDim")
    noContent().build()
  }

  @GET
  @Path("/cleanup")
  def cleanup(): Response = {
    LEDOpsService.gpio.shutdown()
    noContent().build()
  }
}
