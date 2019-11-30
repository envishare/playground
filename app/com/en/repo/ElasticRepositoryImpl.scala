package com.en.repo

import java.util.UUID

import com.en.adapters.HttpRequestAdapter
import com.en.model.Activity
import com.en.util.RequestType
import com.en.util.RequestType.{DELETE, RequestType}
import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.{WSClient, WSResponse}
import play.mvc.Http.Status

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

class ElasticRepositoryImpl @Inject()(wsClient: WSClient)
                                     (implicit ec: ExecutionContext)
  extends ElasticRepository with LazyLogging {
  lazy val httpRequestAdapter = new HttpRequestAdapter(
    wsClient,
    "elasticSearch",
    10 seconds,
    Map.empty,
    "",
    "")

  override def upsertActivity(activity: Activity): Activity = {
    sendElasticRequest(
      activity,
      RequestType.POST,
      "upsertActivity")
    activity
  }

  def sendElasticRequest(
                          activity: Activity,
                          requestType: RequestType,
                          reference: String
                        ): Activity = {
    val body = Json.toJson(activity)
    val url = s"http://157.245.48.99:$port/$indexName/_doc/${activity.id}?refresh=true"
    val response = httpRequestAdapter.sendHttpRequest(
      requestType,
      url,
      body.as[JsObject],
      responseToResultMapping,
      reference
    )
    getActivityById(activity.id) match {
      case Some(updatedActivity) => activity
      case None =>
        requestType match {
          case DELETE => activity
          case _ => throw new RuntimeException(
            s"Error in elastic search " +
              s"operation : $reference," +
              s" requestType: ${requestType.toString}," +
              s" activity: ${activity.toString}")
        }
    }
  }

  private def responseToResultMapping(response: WSResponse): Option[String] = {
    if ((response.status & Status.OK) == Status.OK) {
      (response.json \ "result").asOpt[String]
    } else {
      None
    }
  }

  override def getActivityById(id: String): Option[Activity] = {
    val url = s"http://157.245.48.99:$port/$indexName/_doc/${id}"
    logger.info(url)
    val activity: Option[Activity] = httpRequestAdapter.sendHttpRequest(
      RequestType.GET,
      url,
      JsObject(Seq.empty),
      responseToActivityMapping,
      "getActivityById"
    )
    activity
  }

  private def responseToActivityMapping(response: WSResponse): Option[Activity] = {
    if ((response.status & Status.OK) == Status.OK) {
      logger.info(s"response OK : ${response.toString}")
      (response.json \ "_source").asOpt[Activity]
    } else {
      None
    }
  }

  override def deleteActivity(activity: Activity): Activity = {
    sendElasticRequest(activity, RequestType.DELETE, "deleteActivity")
  }
}
