package com.en.model

import java.time.{Instant, LocalDateTime}
import java.util.UUID

import org.parboiled2.util.Base64
import play.api.libs.json._
import play.api.libs.functional.syntax._

import collection.JavaConverters._

case class User(
                 name: String,
                 lastName: String,
                 userName: String,
                 userId: String = UUID.randomUUID().toString,
                 userSecret: String = UUID.randomUUID().toString,
                 createdAt: Instant = Instant.now(),
                 createdBy: String = UUID.randomUUID().toString,
                 enable: Boolean = true,
                 roles: List[UserRole] = Seq.empty.toList
               )

case class UserRole(
                     roleID: String,
                     roleName: String,
                     permission: List[Permission] = Seq.empty.toList
                   )

case class Permission(
                       permissionType: String,
                       description: String
                     )

object User {

  import UserRole._

  implicit val userReads: Reads[User] = (
    (JsPath \ "userId").read[String] and
      (JsPath \ "name").read[String] and
      (JsPath \ "lastName").read[String] and
      (JsPath \ "userName").read[String] and
      (JsPath \ "userSecret").read[String] and
      (JsPath \ "createdAt").read[Instant] and
      (JsPath \ "createdBy").read[String] and
      (JsPath \ "enable").read[Boolean] and
      (JsPath \ "roles").read[List[UserRole]]
    ) (User.apply _)

  implicit val activityOWrites: OWrites[User] = (
    (JsPath \ 'userId).write[String] and
      (JsPath \ 'name).write[String] and
      (JsPath \ 'lastName).write[String] and
      (JsPath \ 'userName).write[String] and
      (JsPath \ 'userSecret).write[String] and
      (JsPath \ 'createdAt).write[Instant] and
      (JsPath \ 'createdBy).write[String] and
      (JsPath \ 'enable).write[Boolean] and
      (JsPath \ 'roles).write[List[UserRole]]
    ) (unlift(User.unapply))
}

object UserRole {

  import Permission._

  implicit val userRoleReads: Reads[UserRole] = (
    (JsPath \ "roleId").read[String] and
      (JsPath \ "roleName").read[String] and
      (JsPath \ "permission").read[List[Permission]]
    ) (UserRole.apply _)

  implicit val userRoleOWrites: OWrites[UserRole] = (
    (JsPath \ 'roleId).write[String] and
      (JsPath \ 'roleName).write[String] and
      (JsPath \ 'permission).write[List[Permission]]
    ) (unlift(UserRole.unapply))
}

object Permission {
  implicit val permissionReads: Reads[Permission] = (
    (JsPath \ "permissionType").read[String] and
      (JsPath \ "description").read[String]
    ) (Permission.apply _)

  implicit val permissionOWrites: OWrites[Permission] = (
    (JsPath \ 'permissionType).write[String] and
      (JsPath \ 'roleName).write[String]
    ) (unlift(Permission.unapply))
}