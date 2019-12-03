package com.en.services

import java.time.Instant
import java.util.UUID

import com.en.model.ActivityStatus.ActivityStatus
import com.en.model._
import com.en.repo.ElasticRepository
import javax.inject.Inject

import scala.util.Random

trait UserRepo {
  def getUsers(isEnable: Boolean): List[User]
  def getUser(userId: String): Option[User]

  def createUser(
                  userId: String,
                  firstName: String,
                  userLastName: String,
                  userName: String,
                  userSecret: String,
                  userCreatedBy: String
                ): User
}

class UserRepoImpl @Inject()(elasticRepository: ElasticRepository) extends UserRepo {

  override def getUsers(isEnable: Boolean): List[User] = {
    elasticRepository.fetchAllUsers(isEnable).toList
  }

  override def getUser(userId: String): Option[User] = {
    elasticRepository.getUserById(userId)
  }

  override def createUser(
                           userId: String,
                           firstName: String,
                           userLastName: String,
                           userName: String,
                           userSecret: String,
                           userCreatedBy: String
                         ): User = {

    elasticRepository.upsertUser(
      User(
        userId = userId,
        name = firstName,
        lastName = userLastName,
        userName = userName,
        userSecret = userSecret,
        createdAt = Instant.now(),
        createdBy = userCreatedBy,
        enable = true,
        roles = List.empty
      )
    )

  }
}