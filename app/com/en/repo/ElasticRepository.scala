package com.en.repo

import java.util.UUID
import java.util.concurrent.TimeUnit

import com.en.config.ElasticCluster
import com.en.model.{Activity, User}
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.query.QueryBuilders
import play.api.libs.json.Json

import scala.concurrent.duration.Duration

trait ElasticRepository extends ElasticCluster {

  lazy val indexName: String = elasticConfig.documentsIndex
  lazy val indexType: String = elasticConfig.documentsDocType
  lazy val timeout: Duration = elasticConfig.timeoutSeconds

  def upsertActivity(activity: Activity): Activity
  def deleteActivity(activity: Activity): Activity
  def getActivityById(id: String): Option[Activity]
}

