package com.yourspraveen

import scala.concurrent.Await
import scalaxb.{DispatchHttpClientsAsync, SoapClientsAsync}
import scala.concurrent.duration._

//simpe service client to invoke
object ServiceClient {

  def main(args: Array[String]): Unit = {

    val service = new GlobalWeatherSoap12Bindings with SoapClientsAsync with DispatchHttpClientsAsync {}.service

    //val futureResp = service.getWeather(Some("Madras / Minambakkam"), Some("india"))
    val futureResp = service.getCitiesByCountry(Some("india"))

    //this is blocking (blocking is bad)
    val resp = Await.result(futureResp, 5 seconds)

    println(resp.GetCitiesByCountryResult.toString)

    System.exit(0)

  }

}
