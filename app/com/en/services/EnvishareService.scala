package com.en.services

import com.en.model._
import com.en.repo.{ElasticRepository, ElasticRepositoryImpl}
import javax.inject.Inject
import sangria.schema.Action

import scala.util.Random

trait EnvishareService {
  def createActivity(
                      activityName: String,
                      activityContent: String,
                      activityCreatedBy: String
                    ): Activity

  def getActivities(activityStatus: Option[ActivityStatus.Value]): List[Activity]

  def getActivity(id: String): Option[Activity]

  def getUsers(isEnable: Boolean): List[User]

  def getRoles: List[UserRole]
}

class EnvishareServiceImpl @Inject()(activityRepo: ActivityRepo)
  extends EnvishareService {

  override def createActivity(
                               activityName: String,
                               activityContent: String,
                               activityCreatedBy: String
                             ): Activity = {
    activityRepo.createActivity(
      name = activityName,
      content = activityContent,
      createdBy = activityCreatedBy
    )
  }

  override def getActivities(
                              activityStatus: Option[ActivityStatus.Value]
                            ): List[Activity] = {
    activityRepo.getAllActivities(activityStatus.getOrElse(ActivityStatus.PENDING))
  }

  override def getActivity(id: String): Option[Activity] =
    activityRepo.findActivityById(id)

  override def getUsers(isEnable: Boolean): List[User] =
    UserService.users.filter(_.enable == isEnable).toList

  override def getRoles: List[UserRole] =
    UserService.roles.toList
}

