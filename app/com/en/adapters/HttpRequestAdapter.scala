package com.en.adapters

import akka.io.dns.DnsProtocol
import com.en.util.RequestType.{DELETE, GET, POST, PUT, RequestType}
import com.typesafe.scalalogging.LazyLogging
import org.apache.commons.lang3.ThreadUtils
import play.api.libs.json.JsObject
import play.api.libs.ws.{WSAuthScheme, WSClient, WSRequest, WSResponse}
import play.libs.Files.DelegateTemporaryFile

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration

class HttpRequestAdapter(
                          wsClient: WSClient,
                          targetApiName: String,
                          timeout: Duration = Duration.Inf,
                          httpHeaders: Map[String, String] = Map.empty[String, String],
                          user: String,
                          pass: String
                        )(implicit ec: ExecutionContext) extends LazyLogging {

  private def withDefaultHeaders(request: WSRequest): WSRequest = {
    request
      .addHttpHeaders("Content-Type" -> "application/json")
  }

  def sendHttpRequest[T](
               requestType: RequestType,
               url: String,
               body: JsObject,
               responseConverter: WSResponse => Option[T],
               reference: String
             ): Option[T] = {
    val request = wsClient.url(url)
    if (request == null) {
      None
    } else {
      val complexRequest: WSRequest =
        withDefaultHeaders(request)
          .withRequestTimeout(timeout)
          .withAuth(user, pass, WSAuthScheme.BASIC)

      val futureResponse: Future[WSResponse] = requestType match {
        case PUT => complexRequest.put(body)
        case GET => complexRequest.get()
        case POST => complexRequest.post(body)
        case DELETE => complexRequest.delete()
      }
      val response = Await.result[WSResponse](futureResponse, timeout)
      responseConverter(response)
    }
  }
}

