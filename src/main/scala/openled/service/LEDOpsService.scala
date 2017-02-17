package openled.service

import com.pi4j.io.gpio._

/**
  * Created by cts1 on 9/2/17.
  */
object LEDOpsService {

  val gpio = GpioFactory.getInstance()

  private val redLED: String = "Red LED"

  def gpioLEDPin(): GpioPinDigitalOutput = {
    if (gpio.getProvisionedPin(redLED) == null)
      gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, redLED)
    else {
      gpio.getProvisionedPin(redLED).asInstanceOf[GpioPinDigitalOutput]
    }
  }
}
