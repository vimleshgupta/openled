package openled.service

import java.util
import scala.util.control.Breaks._


import com.pi4j.io.gpio.{GpioFactory, GpioPinDigitalInput, GpioPinDigitalOutput, RaspiPin}

object ElectricityServie {

  val trig = RaspiPin.GPIO_07
  val light = RaspiPin.GPIO_02
  val echo = RaspiPin.GPIO_00
  val trigId = "trig"
  val echoId = "echo"
  val lightId = "light"
  val gpio = GpioFactory.getInstance()
  var gpioTrig: GpioPinDigitalOutput = _
  var gpioLight: GpioPinDigitalOutput = _
  var gpioEcho: GpioPinDigitalInput = _

  var isSenssorEnabled = false

  def init(): Unit = {

    gpioTrig = if (gpio.getProvisionedPin(trigId) == null)
      gpio.provisionDigitalOutputPin(trig, trigId)
    else {
      gpio.getProvisionedPin(trigId).asInstanceOf[GpioPinDigitalOutput]
    }

    gpioEcho = if (gpio.getProvisionedPin(echoId) == null)
      gpio.provisionDigitalInputPin(echo, echoId)
    else gpio.getProvisionedPin(echoId).asInstanceOf[GpioPinDigitalInput]

    gpioLight = if (gpio.getProvisionedPin(lightId) == null)
      gpio.provisionDigitalOutputPin(light, lightId)
    else {
      gpio.getProvisionedPin(lightId).asInstanceOf[GpioPinDigitalOutput]
    }

    gpioTrig.setState(false)

  }

  def trigger(): Double = {

    //    println("starting measurement...")

    gpioTrig.setState(true)
    var start = System.nanoTime()
    while (start + 10000l >= System.nanoTime()) {}

    gpioTrig.setState(false)

    var count = 0
    while (gpioEcho.isLow) {
      count += 1
    }
    //    println("got count 1 " + count)
    start = System.nanoTime()

    count = 0
    while (gpioEcho.isHigh) {
      count += 1
    }
    //    println("got count 2 " + count)

    val stop = System.nanoTime()

    val SPEEDOFSOUND = 34029
    val delta = (stop - start);
    val distance = delta * SPEEDOFSOUND;


    val dist = distance / 2.0 / (1000000000L)
    println(dist)
    //    println (((stop - start)/1000000000D) * 17000D)

    dist
  }

  def on(): Unit = {

    if (gpioLight.isLow)
      gpioLight.setState(true)
  }

  def off(): Unit = {
    if (gpioLight.isHigh)
      gpioLight.setState(false)
  }


  def cleanup() = {
    isSenssorEnabled = false
    off()
    gpio.shutdown()
  }

  def startSensor(): Unit = {

    if (!isSenssorEnabled) {
      isSenssorEnabled = true
      val runnable = new Runnable {
        override def run(): Unit = {

          while (isSenssorEnabled) {

            val distance = trigger()

            if (distance < 120D || distance > 130D) {
              on()
            } else {
              off()
            }

            Thread.sleep(2000)
          }

        }
      }

      val thread = new Thread(runnable)
      thread.start()

    }
  }

  def findAcurateDistance(): Double = {

    val d: Array[Double] = new Array[Double](5)
    for (i <- 1 to 5) {
      d(i) = trigger()
    }

    val data = new util.ArrayList[util.List[Double]]()
    d.foreach { v =>
      var isAdded = false
      breakable {
        data.forEach { dd =>

          if (isTolratable(v, dd.get(0))) {
            dd.add(v)
            isAdded = true
            break
          }
        }
      }
      if (!isAdded) data.add(new util.ArrayList[Double]() {
        add(v)
      })
    }

    null
  }

  def isTolratable(data: Double, acutual: Double): Boolean = {
    val diff = data - acutual
    if (diff >= 0) {
      diff < 5
    } else {
      diff > -5
    }
  }
}
