package models

import java.time.{Instant, LocalDateTime}
import java.util.UUID

import ActivityStatus._

trait IActivity {
  def id: String
  def name: Option[String]
  def createdAt: Instant
  def createdBy: String
  def modifiedBy: String
  def modifiedAt: Instant
  def activityReference: ActivityReference
  def review: List[ActivityReview]
}

trait IActivityReview {
  def id: Long
  def review: String
}

trait IActivityRefernce {
  def parentActivity: Option[IActivity]
  def childActivity: Option[IActivity]
}

case class Activity(
    id: String,
    name: Option[String],
    createdAt: Instant = Instant.now,
    createdBy: String = UUID.randomUUID().toString,
    modifiedBy: String = UUID.randomUUID().toString,
    modifiedAt: Instant = Instant.now,
    activityReference: ActivityReference = ActivityReference.empty,
    review: List[ActivityReview] = Seq.empty.toList
) extends IActivity

case class ActivityReference(
    parentActivity: Option[Activity],
    childActivity: Option[Activity]
) extends IActivityRefernce

object ActivityReference {
  val empty = ActivityReference(
    parentActivity = None,
    childActivity = None
  )
}

case class ActivityReview(
    id: Long,
    review: String
) extends IActivityReview
