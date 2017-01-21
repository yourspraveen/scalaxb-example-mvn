package com.yourspraveen

import scala.concurrent.Await
import scalaxb.{DispatchHttpClientsAsync, SoapClientsAsync}
import scala.concurrent.duration._

/**
  * Created by praveen on 1/20/17.
  */
object ServiceClient {

  def main(args: Array[String]): Unit = {

    val service = new GlobalWeatherSoap12Bindings with SoapClientsAsync with DispatchHttpClientsAsync {}.service

    //val futureResp = service.getWeather(Some("Madras / Minambakkam"), Some("india"))
    val futureResp = service.getCitiesByCountry(Some("india"))

    val resp = Await.result(futureResp, 5 seconds)

    println(resp.GetCitiesByCountryResult.toString)

  }

}
