package com.yourspraveen

import java.util.concurrent.{ExecutorService, Executors}

import org.slf4j.LoggerFactory
import com.typesafe.scalalogging._

import scalaxb.{DispatchHttpClientsAsync, SoapClientsAsync}
import scala.concurrent.duration._
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

object CakePatternServiceInvoker {
  val logger = Logger(LoggerFactory.getLogger("name"))

  def main(args: Array[String]): Unit = {

    val service = new GlobalWeatherSoap12Bindings with SoapClientsAsync with SecureDispatchHttpClientsAsync {}.service

    val futureResp = service.getWeather(Some("Nashville, Nashville International Airport"), Some("United States"))

    futureResp.onComplete{
      case Success(resp) => logger.debug(resp.GetWeatherResult.toString)
      case Failure(e) => logger.debug(s"Error: $e")
    }(global)

    //shutting down dispatch client this will not stop the program execution
    dispatch.Http.shutdown

    //on completion of all processing
    System.exit(0)
  }
}

//to add headers to soap envelope header
trait SecureDispatchHttpClientsAsync extends DispatchHttpClientsAsync with LazyLogging{

  //overriding default values with what we need
  override lazy val httpClient = new SecureDispatchHttpClient {}

  //doubling timeouts from default values
  override def requestTimeout: Duration = 120.seconds
  override def connectionTimeout: Duration = 10.seconds

  //Client will add the headers and pass it to super
  trait SecureDispatchHttpClient extends DispatchHttpClient {

    //only overriding what we need and leaving the rest to default implementation
    override def request(in: String, address: java.net.URI, headers: Map[String, String]): concurrent.Future[String] = {

      //to debug the request
      logger.debug(address.toString)
      logger.debug(in.toString)
      logger.debug("Headers : "+ headers.toString())

      //add additional headers for suthentication as plain text avoid this!!!
      val additionalHeaders = headers ++ Map[String, String]("username" -> "uname", "password" -> "pword")

      //passing back the customized header
      super.request(in, address, additionalHeaders)
    }
  }

}
