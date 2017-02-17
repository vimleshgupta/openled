package openled.service

import com.pi4j.component.switches.{SwitchState, SwitchStateChangeEvent}
import com.pi4j.component.switches.impl.GpioMomentarySwitchComponent
import com.pi4j.io.gpio.PinState._
import com.pi4j.io.gpio._
import com.pi4j.wiringpi.SoftPwm

case class PushpinService() {

  val gpio = GpioFactory.getInstance()

  val redLEDPinNumber = RaspiPin.GPIO_01.getAddress
  val ledMinValue = 0
  val ledMaxValue = 100
  var ledCurrentValue = 0

  var led: GpioPinDigitalOutput = _
  var pushPin1: GpioPinDigitalInput = _
  var pushPin2: GpioPinDigitalInput = _

  def init(): Unit = {

    led = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01)
    pushPin1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_05, PinPullResistance.PULL_UP)
    pushPin2 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_06, PinPullResistance.PULL_UP)

    createSoftPwm()
    setupMomentarySwitchComponentToUpLED(led, pushPin1)
    setupMomentarySwitchComponentToDownLED(led, pushPin2)
  }

  def createSoftPwm() = {
    SoftPwm.softPwmCreate(redLEDPinNumber, ledCurrentValue, ledMaxValue)
  }

  def setupMomentarySwitchComponentToUpLED(led: GpioPinDigitalOutput, pushPin: GpioPinDigitalInput) = {
    val momentarySwitchComponent = new GpioMomentarySwitchComponent(pushPin, HIGH, LOW)
    momentarySwitchComponent.addListener((event: SwitchStateChangeEvent) => {
      if (event.getNewState == SwitchState.ON) {
        if (ledCurrentValue < ledMaxValue) {
          ledCurrentValue += 10
          SoftPwm.softPwmWrite(redLEDPinNumber, ledCurrentValue)
          println("up")
        }
      }
    })
  }

  def setupMomentarySwitchComponentToDownLED(led: GpioPinDigitalOutput, pushPin: GpioPinDigitalInput) = {
    val momentarySwitchComponent = new GpioMomentarySwitchComponent(pushPin, HIGH, LOW)
    momentarySwitchComponent.addListener((event: SwitchStateChangeEvent) => {
      if (event.getNewState == SwitchState.ON) {
        if (ledCurrentValue > ledMinValue) {
          ledCurrentValue -= 10
          SoftPwm.softPwmWrite(redLEDPinNumber, ledCurrentValue)
          println("down")
        }
      }
    })
  }
}

object PushpinService {
  var instance: Option[PushpinService] = None

  def setup() = {
    if (instance.isDefined) {
      instance = Some(PushpinService())
      instance.get.init()
    }
  }

  def cleanup() = {
    if (instance.isDefined) {
      instance.get.gpio.shutdown()
      instance = None
    }
  }
}