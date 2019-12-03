package com.en

package object model {
case class InvalidActivityIdException(activityId: String) extends IllegalStateException(
    s"Invalid activity Id $activityId"
  )
}
