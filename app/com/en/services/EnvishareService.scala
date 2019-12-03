package com.en.services

import java.time.Instant

import com.en.model.ActivityStatus.ActivityStatus
import com.en.model._
import com.en.repo.{ElasticRepository, ElasticRepositoryImpl}
import javax.inject.Inject
import sangria.schema.Action

import scala.util.Random

trait EnvishareService {
  def deleteActivityAttachment(
                                activityId: String,
                                attachment: String,
                                activityModifiedBy: String
                              ): Activity

  def addActivityAttachment(
                             activityId: String,
                             attachment: String,
                             activityModifiedBy: String
                           ): Activity

  def createUser(
                  userId: String,
                  firstName: String,
                  userLastName: String,
                  userName: String,
                  userSecret: String,
                  userCreatedBy: String
                ): User

  def deleteReview(activityId: String, reviewId: String, modifiedBy: String): Activity

  def deleteActivity(activityId: String): Activity

  def addReview(activityId: String, reviewDetails: String, activityModifiedBy: String): Activity

  def updateReview(
                    activityId: String,
                    reviewId: String,
                    reviewDetails: String,
                    activityModifiedBy: String
                  ): Activity

  def createActivity(
                      activityName: String,
                      activityContent: String,
                      activityCreatedBy: String
                    ): Activity

  def updateActivity(
                      activityId: String,
                      activityName: String,
                      activityContent: String,
                      activityStatus: ActivityStatus,
                      activityModifiedBy: String
                    ): Activity

  def getActivities(activityStatus: ActivityStatus): List[Activity]

  def getActivity(id: String): Option[Activity]

  def getUsers(isEnable: Boolean): List[User]
  def getUser(userId: String): Option[User]

  def getRoles: List[UserRole]
}

class EnvishareServiceImpl @Inject()(activityRepo: ActivityRepo, userRepo: UserRepo)
  extends EnvishareService {


  override def deleteActivityAttachment(
                                         activityId: String,
                                         attachment: String,
                                         activityModifiedBy: String
                                       ): Activity = {
    activityRepo.removeAttachment(activityId, attachment, activityModifiedBy)
  }

  override def addActivityAttachment(
                                      activityId: String,
                                      attachment: String,
                                      activityModifiedBy: String
                                    ): Activity = {
    activityRepo.addAttachment(activityId, attachment, activityModifiedBy)
  }

  override def createUser(
                           userId: String,
                           firstName: String,
                           userLastName: String,
                           userName: String,
                           userSecret: String,
                           userCreatedBy: String
                         ): User = {
    userRepo.createUser(
      userId,
      firstName,
      userLastName,
      userName,
      userSecret,
      userCreatedBy
    )
  }

  override def deleteReview(activityId: String, reviewId: String, modifiedBy: String): Activity = {
    activityRepo.deleteReview(activityId, reviewId, modifiedBy)
  }

  override def deleteActivity(activityId: String): Activity = {
    activityRepo.deleteActivity(activityId)
  }

  override def addReview(
                          activityId: String,
                          reviewDetails: String,
                          activityModifiedBy: String
                        ): Activity = {
    activityRepo.addReview(activityId, reviewDetails, activityModifiedBy)
  }


  override def updateReview(
                             activityId: String,
                             reviewId: String,
                             reviewDetails: String,
                             activityModifiedBy: String
                           ): Activity = {
    activityRepo.updateReview(activityId, reviewId, reviewDetails, activityModifiedBy)
  }

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


  override def updateActivity(
                               activityId: String,
                               activityName: String,
                               activityContent: String,
                               activityStatus: ActivityStatus,
                               activityModifiedBy: String
                             ): Activity = {
    activityRepo.updateActivity(
      id = activityId,
      name = activityName,
      content = activityContent,
      status = activityStatus,
      modifiedBy = activityModifiedBy
    )
  }

  override def getActivities(
                              activityStatus: ActivityStatus
                            ): List[Activity] = {
    activityRepo.getAllActivities(activityStatus)
  }

  override def getActivity(id: String): Option[Activity] =
    activityRepo.findActivityById(id)

  override def getUsers(isEnable: Boolean): List[User] =
    userRepo.getUsers(isEnable)

  override def getUser(userId: String): Option[User] =
    userRepo.getUser(userId)

  override def getRoles: List[UserRole] =
    UserService.roles.toList
}

