package com.en.repo

import java.util.UUID

import com.en.adapters.HttpRequestAdapter
import com.en.model.{Activity, User}
import com.en.util.RequestType
import com.en.util.RequestType.{DELETE, RequestType}
import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}
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
    1000000 seconds,
    Map.empty,
    "",
    "")

  override def upsertActivity(activity: Activity): Activity = {
    sendElasticRequestToActivityIndex(
      activity,
      RequestType.POST,
      "upsertActivity")
  }

  override def deleteActivity(activity: Activity): Activity = {
    sendElasticRequestToActivityIndex(activity, RequestType.DELETE, "deleteActivity")
  }

  private def sendElasticRequestToActivityIndex(
                                                 activity: Activity,
                                                 requestType: RequestType,
                                                 reference: String
                                               ): Activity = {
    val body = Json.toJson(activity)
    val url = getUrl(activityIndex, Some(activity.id))
    val response = httpRequestAdapter.sendHttpRequest(
      requestType,
      url,
      body.as[JsObject],
      responseToResultMapping,
      reference
    )
    getActivityById(activity.id) match {
      case Some(updatedActivity) => updatedActivity
      case None => requestType match {
        case DELETE => activity
        case _ => throw ElasticSearchOperationException(reference, requestType, body)
      }
    }
  }

  override def getActivityById(id: String): Option[Activity] = {
    val url = getUrl(activityIndex, Some(id))
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

  override def upsertUser(user: User): User = {
    sendElasticRequestToUserIndex(
      user,
      RequestType.POST,
      "upsertUser")
  }

  private def sendElasticRequestToUserIndex(
                                             user: User,
                                             requestType: RequestType,
                                             reference: String
                                           ): User = {
    val body = Json.toJson(user)
    val url = getUrl(userIndex, Some(user.userId))
    val response = httpRequestAdapter.sendHttpRequest(
      requestType,
      url,
      body.as[JsObject],
      responseToResultMapping,
      reference
    )
    getUserById(user.userId) match {
      case Some(updatedUser) => updatedUser
      case None => requestType match {
        case DELETE => user
        case _ => throw ElasticSearchOperationException(reference, requestType, body)
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

  override def getUserById(id: String): Option[User] = {
    val url = getUrl(activityIndex, Some(id))
    logger.info(url)
    val activity: Option[User] = httpRequestAdapter.sendHttpRequest(
      RequestType.GET,
      url,
      JsObject(Seq.empty),
      responseToUserMapping,
      "getActivityById"
    )
    activity
  }

  private def responseToUserMapping(response: WSResponse): Option[User] = {
    if ((response.status & Status.OK) == Status.OK) {
      logger.info(s"response OK : ${response.toString}")
      (response.json \ "_source").asOpt[User]
    } else {
      None
    }
  }

  private def getUrl(
                      indexName: String,
                      id: Option[String] = None,
                      isForSearch: Boolean = true
                    ): String = {
    val elasticHost = hosts.head.toString
    if (isForSearch && id.nonEmpty) {
      s"http://ecotrender.com:$port/$indexName/_doc/$id?refresh=true"
    } else {
      s"http://ecotrender.com:$port/$indexName/_doc/_search/"
    }
  }

  override def fetchAllUsers(isEnableUsers: Boolean): Seq[User] = {
    import Json.{arr, obj}
    val query = obj("query" -> obj(
      "bool" -> obj(
        "must" -> JsArray(
          Seq(
            obj("match" -> obj("enable" -> s"$isEnableUsers"))
          )
        )
      )
    )
    )
    val url = getUrl(userIndex, None, true)
    httpRequestAdapter.sendHttpRequest(
      RequestType.POST,
      url,
      query,
      responseToUsersMapping,
      "getActivityById"
    ).get
  }

  private def responseToUsersMapping(response: WSResponse): Option[Seq[User]] = {
    if ((response.status & Status.OK) == Status.OK) {
      logger.info(s"response OK : ${response.toString}")
      println(response.json)
      val users = (response.json \ "hits" \ "hits").as[JsArray]
        .value.flatMap(v => (v \ "_source").asOpt[User])
      Some(users)
    } else {
      None
    }
  }
}

case class ElasticSearchOperationException(
                                            reference: String,
                                            requestType: RequestType,
                                            jsValue: JsValue
                                          ) extends IllegalStateException(
  s"Error in elastic search operation : $reference, requestType: ${requestType.toString}," +
    s" activity: ${jsValue.toString}"
)

