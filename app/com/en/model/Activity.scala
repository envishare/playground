package com.en.model

import java.time.{Instant, LocalDateTime}
import java.util
import java.util.UUID

import play.api.libs.functional.syntax._
import play.api.libs.json._
import collection.JavaConverters._

trait IActivity {
  def id: String

  def name: Option[String]

  def content: String

  def createdAt: Instant

  def createdBy: String

  def modifiedBy: Option[String]

  def modifiedAt: Option[Instant]

  def reviews: List[ActivityReview]
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
                     id: String = UUID.randomUUID().toString,
                     name: Option[String],
                     content: String = "",
                     createdAt: Instant = Instant.now,
                     createdBy: String = UUID.randomUUID().toString,
                     modifiedAt: Option[Instant] = Some(Instant.now),
                     modifiedBy: Option[String] = Some(UUID.randomUUID().toString),
                     reviews: List[ActivityReview] = Seq.empty.toList
                   ) extends IActivity

case class ActivityReference(
                              parentActivity: Option[Activity],
                              childActivity: Option[Activity]
                            ) extends IActivityRefernce

case class ActivityReview(id: Long, review: String) extends IActivityReview

object Activity {

  import ActivityReference._
  import ActivityReview._

  def fromMap(results: util.Map[String, AnyRef]): Option[Activity] = {
    val activityFieldMap = results.asScala
    (
      activityFieldMap.get("id"),
      activityFieldMap.get("name"),
      activityFieldMap.get("content"),
      activityFieldMap.get("createdAt"),
      activityFieldMap.get("createdBy"),
      activityFieldMap.get("modifiedAt"),
      activityFieldMap.get("modifiedBy"),
      activityFieldMap.get("activityReference"),
      activityFieldMap.get("reviews")
    ) match {
      case (
        Some(id),
        name,
        Some(content),
        Some(createdAt),
        Some(createdBy),
        modifiedAt,
        modifiedBy,
        Some(activityReference),
        Some(reviews)
        ) =>
        Some(
          Activity(
            id.toString,
            name.map(_.toString),
            content.toString,
            Instant.parse(createdAt.toString),
            createdBy.toString,
            modifiedAt.map(d => Instant.parse(d.toString)),
            modifiedBy.map(m => m.toString),
            Seq.empty.toList
          ))
      case _ =>
        throw new RuntimeException(
          "Unable to convert Map values to Activity Object.")
    }
  }

  implicit val activityReads: Reads[Activity] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "name").readNullable[String] and
      (JsPath \ "content").read[String] and
      (JsPath \ "createdAt").read[Instant] and
      (JsPath \ "createdBy").read[String] and
      (JsPath \ "modifiedAt").readNullable[Instant] and
      (JsPath \ "modifiedBy").readNullable[String] and
      (JsPath \ "reviews").read[List[ActivityReview]]
    ) (Activity.apply _)

  implicit val activityOWrites: OWrites[Activity] = (
    (JsPath \ 'id).write[String] and
      (JsPath \ 'name).writeNullable[String] and
      (JsPath \ 'content).write[String] and
      (JsPath \ 'createdAt).write[Instant] and
      (JsPath \ 'createdBy).write[String] and
      (JsPath \ 'modifiedAt).writeNullable[Instant] and
      (JsPath \ 'modifiedBt).writeNullable[String] and
      (JsPath \ 'reviews).write[List[ActivityReview]]
    ) (unlift(Activity.unapply))
}

object ActivityReference {

  implicit val activityReferenceReads: Reads[ActivityReference] = (
    (JsPath \ "parentActivity").readNullable[Activity] and
      (JsPath \ "childActivity").readNullable[Activity]
    ) (ActivityReference.apply _)

  implicit val activityReferenceWrites: OWrites[ActivityReference] = (
    (JsPath \ 'parentActivity).writeNullable[Activity] and
      (JsPath \ 'childActivity).writeNullable[Activity]
    ) (unlift(ActivityReference.unapply))

  val empty = ActivityReference(parentActivity = None, childActivity = None)

  def fromMap(results: util.Map[String, AnyRef]): Option[ActivityReference] = {

    val activityFieldMap = results.asScala
    (
      activityFieldMap.get("parentActivity"),
      activityFieldMap.get("childActivity")
    ) match {
      case (parentActivity, childActivity) =>
        Some(
          ActivityReference(
            parentActivity.map(p => Activity.fromMap(p.asInstanceOf[util.Map[String, AnyRef]])).get,
            childActivity.map(p => Activity.fromMap(p.asInstanceOf[util.Map[String, AnyRef]])).get
          ))

      case _ => throw new RuntimeException("Unable to convert Map values to Activity Object.")
    }
  }
}

object ActivityReview {
  implicit val activityReviewReads: Reads[ActivityReview] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "review").read[String]
    ) (ActivityReview.apply _)

  implicit val activityReviewOWrites: OWrites[ActivityReview] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "review").write[String]
    ) (unlift(ActivityReview.unapply))

  def fromMap(results: util.Map[String, AnyRef]): Option[ActivityReview] = {

    val activityFieldMap = results.asScala
    (activityFieldMap.get("id"), activityFieldMap.get("review")) match {
      case (Some(id), Some(review)) =>
        Some(ActivityReview(id.toString.toLong, review.toString))
      case _ =>
        throw new RuntimeException("Unable to convert Map values to Activity Object.")
    }
  }
}
