package models

import scala.util.Random

class EnvishareService {
  import models.ActivityRepo._

  def getActivities(
      activityStatus: Option[ActivityStatus.Value]): List[Activity] =
    ActivityRepo.activities

  def getActivity(id: String): Option[Activity] =
    ActivityRepo.activities.find(c ⇒ c.id == id)

  def getUsers(isEnable: Boolean): List[User] =
    UserService.users.filter(_.enable == isEnable).toList

  def getRoles: List[UserRole] =
    UserService.roles.toList
}

