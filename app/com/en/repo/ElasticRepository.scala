package com.en.repo

import com.en.config.ElasticCluster
import com.en.model.{Activity, User}
import scala.concurrent.duration.Duration

trait ElasticRepository extends ElasticCluster {

  lazy val activityIndex: String = elasticConfig.activityIndex
  lazy val userIndex: String = elasticConfig.activityIndex
  lazy val indexType: String = elasticConfig.documentsDocType
  lazy val timeout: Duration = elasticConfig.timeoutSeconds

  def upsertActivity(activity: Activity): Activity
  def deleteActivity(activity: Activity): Activity
  def upsertUser(user: User): User
  def getUserById(id: String): Option[User]
  def fetchAllUsers(isEnableUsers: Boolean): Seq[User]
  def getActivityById(id: String): Option[Activity]
}

