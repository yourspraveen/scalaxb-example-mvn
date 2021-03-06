package com.yourspraveen

import org.slf4j.LoggerFactory
import com.typesafe.scalalogging._
import dispatch.{as, url}

import scalaxb.{DispatchHttpClientsAsync, SoapClientsAsync}
import scala.concurrent.duration._
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

object CakePatternServiceInvoker {
  val logger = Logger(LoggerFactory.getLogger("CakePatternServiceInvoker"))

  def main(args: Array[String]): Unit = {

    val service = new GlobalWeatherSoap12Bindings with SoapClientsAsync with SecureDispatchHttpClientsAsync {}.service

    val futureResp = service.getWeather(Some("Nashville, Nashville International Airport"), Some("United States"))

    futureResp.onComplete{
      case Success(resp) => logger.info(resp.GetWeatherResult.toString)
        shutdown
      case Failure(e) => logger.error(s"Error: $e")
        shutdown
    }(global)



  }

  def shutdown = {
    //shutting down dispatch client this will not stop the program execution
    dispatch.Http.shutdown

    //on completion of all processing
    System.exit(0)
  }
}

//to add headers to soap envelope header using cake pattern
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

      //add additional headers as plain text avoid this!!!
      val additionalHeaders = headers ++ Map[String, String]("token" -> "token")

      //passing back the customized header
      val req = url(address.toString).as_!("user","Password") //basic auth
        .setBodyEncoding("UTF-8") <:< additionalHeaders << in
      http(req > as.String)
    }
  }

}
