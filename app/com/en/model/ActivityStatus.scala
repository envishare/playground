package com.en.model

import play.api.libs.json.{JsError, JsString, JsSuccess, Reads, Writes}

object ActivityStatus extends Enumeration {
  type ActivityStatus = Value
  val PENDING, VERIFIED, DELETED = Value
  val all = Seq(PENDING, VERIFIED, DELETED)

  def from(anyRef: Option[AnyRef]): ActivityStatus = {
    anyRef.map(a => from(a.toString)).get
  }

  def from(statusString: String): ActivityStatus = {
    all.find(_.toString == statusString) match {
      case Some(value) => value
      case None => throw InvalidActivityStatusException(statusString)
    }
  }

  implicit val activityStatusReads: Reads[ActivityStatus] = {
    case JsString(value) => JsSuccess(from(value))
    case nonJsString => JsError(s"$nonJsString is not JsString")
  }

  implicit val activityStatusWrites: Writes[ActivityStatus] = status => {
    JsString(status.toString)
  }
}

case class InvalidActivityStatusException(statusString: String)
  extends IllegalStateException {
  s"Unable to convert string$statusString to ActivityStatus"
}
