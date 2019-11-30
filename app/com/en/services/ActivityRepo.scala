package com.en.services

import java.time.Instant
import java.util.UUID

import com.en.model.ActivityStatus.ActivityStatus
import com.en.model._
import com.en.repo.ElasticRepository
import javax.inject.Inject

import scala.util.Random

trait ActivityRepo {
  def findActivityById(id: String): Option[Activity]
  def createActivity(name: String, content: String, createdBy: String): Activity
  def getAllActivities(status: ActivityStatus): List[Activity]
}

class ActivityRepoImpl @Inject()(elasticRepository: ElasticRepository) extends ActivityRepo{

  override def findActivityById(id: String): Option[Activity] = {
    elasticRepository.getActivityById(id)
  }

  def createActivity(name: String, content: String, createdBy: String): Activity = {
    val activity = Activity(
      UUID.randomUUID().toString,
      Some(name),
      content,
      createdAt = Instant.now(),
      createdBy,
      modifiedAt = None,
      modifiedBy = None,
      reviews = List.empty
    )
    elasticRepository.upsertActivity(activity)
  }

  def getAllActivities(status: ActivityStatus): List[Activity] = {
    val activities = List(
      Activity(
        id = UUID.randomUUID().toString,
        name = Some("Lake Cleanup"),
        reviews = Seq(
          ActivityReview(
            id = Random.nextLong() % 100,
            review = Random.nextString(20)
          )
        ).toList
      )
    )
    activities.map(elasticRepository.upsertActivity)
    activities
  }
}