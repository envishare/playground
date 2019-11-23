package models

object ActivityStatus extends Enumeration {
  type ActivityStatus = Value
  val PENDING, VERIFIED, DELETED = Value
}
