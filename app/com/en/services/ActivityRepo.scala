package com.en.services

import java.time.Instant
import java.util.UUID

import com.en.model.ActivityStatus.ActivityStatus
import com.en.model._
import com.en.repo.ElasticRepository
import javax.inject.Inject

import scala.util.Random

trait ActivityRepo {
  def removeAttachment(activityId: String, attachment: String, activityModifiedBy: String): Activity

  def addAttachment(activityId: String, attachment: String, activityModifiedBy: String): Activity


  def updateActivity(
                      id: String,
                      name: String,
                      status: ActivityStatus,
                      content: String,
                      modifiedBy: String
                    ): Activity

  def deleteReview(
                    activityId: String,
                    reviewId: String,
                    activityModifiedBy: String
                  ): Activity

  def updateReview(
                    activityId: String,
                    reviewId: String,
                    reviewDetails: String,
                    activityModifiedBy: String
                  ): Activity

  def deleteActivity(activityId: String): Activity

  def addReview(activityId: String, reviewDetails: String, activityModifiedBy: String): Activity

  def findActivityById(id: String): Option[Activity]

  def createActivity(name: String, content: String, createdBy: String): Activity

  def getAllActivities(status: ActivityStatus): List[Activity]
}

class ActivityRepoImpl @Inject()(elasticRepository: ElasticRepository) extends ActivityRepo {

  override def addAttachment(
                                 activityId: String,
                                 attachment: String,
                                 activityModifiedBy: String
                               ): Activity = {
    val activity = getActivity(activityId)
    val updatedActivity = activity.copy(
      attachments = (activity.attachments :+ attachment).toSet.toList
    )
    elasticRepository.upsertActivity(updatedActivity)
  }

  override def removeAttachment(
                              activityId: String,
                              attachment: String,
                              activityModifiedBy: String
                            ): Activity = {
    val activity = getActivity(activityId)
    val updatedActivity = activity.copy(
      attachments = activity.attachments.filterNot(_ == attachment)
    )
    elasticRepository.upsertActivity(updatedActivity)
  }

  override def deleteReview(
                             activityId: String,
                             reviewId: String,
                             activityModifiedBy: String
                           ): Activity = {
    val activity = getActivity(activityId)
    val updatedActivity = activity.copy(
      reviews = activity.reviews.filterNot(_.id == reviewId.toLong),
      modifiedBy = Some(activityModifiedBy),
      modifiedAt = Some(Instant.now())
    )
    elasticRepository.upsertActivity(updatedActivity)
  }

  private def getActivity(id: String): Activity = {
    elasticRepository.getActivityById(id) match {
      case Some(activity) => activity
      case None => throw InvalidActivityIdException(id)
    }
  }

  override def updateReview(
                             activityId: String,
                             reviewId: String,
                             reviewDetails: String,
                             activityModifiedBy: String
                           ): Activity = {
    val activity = getActivity(activityId)
    val updatedActivity = activity.copy(
      reviews = activity.reviews.map(
        m => if (m.id == reviewId.toLong) {
          m.copy(review = reviewDetails)
        } else {
          m
        }
      ),
      modifiedBy = Some(activityModifiedBy),
      modifiedAt = Some(Instant.now())
    )
    elasticRepository.upsertActivity(updatedActivity)
  }

  override def deleteActivity(activityId: String): Activity = {
    elasticRepository.deleteActivity(getActivity(activityId))
  }

  override def addReview(
                          activityId: String,
                          reviewDetails: String,
                          activityModifiedBy: String
                        ): Activity = {

    val newReview = ActivityReview(Random.nextInt().toLong, reviewDetails)
    val activity = getActivity(activityId)
    val newReviewList: List[ActivityReview] = activity.reviews :+ newReview
    val updatedActivity = activity.copy(reviews = newReviewList)
      .copy(modifiedBy = Some(activityModifiedBy))
      .copy(modifiedAt = Some(Instant.now()))

    elasticRepository.upsertActivity(updatedActivity)
  }

  override def findActivityById(id: String): Option[Activity] = {
    elasticRepository.getActivityById(id)
  }

  def updateActivity(
                      activityId: String,
                      activityName: String,
                      activityStatus: ActivityStatus,
                      activityContent: String,
                      activityModifiedBy: String
                    ): Activity = {
    val activity = getActivity(activityId)
    val modifiedActivity = activity.copy(
      name = Some(activityName),
      content = activityContent,
      status = activityStatus,
      modifiedBy = Some(activityModifiedBy),
      modifiedAt = Some(Instant.now())
    )

    elasticRepository.upsertActivity(modifiedActivity)
  }

  def createActivity(name: String, content: String, createdBy: String): Activity = {
    val activity = Activity(
      UUID.randomUUID().toString,
      Some(name),
      content,
      ActivityStatus.PENDING,
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