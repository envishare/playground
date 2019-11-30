package com.en.services

import java.util.UUID

import com.en.model._

object UserService {

  val permissions: Seq[Permission] = Seq(
    Permission("View Only", "View only and no status can be changed"),
    Permission("VerifierOn", "Ability to verify the activities"),
    Permission("ReviwOn", "Ability to review activities"),
  )

  val roles: Seq[UserRole] = Seq(
    UserRole(roleID = UUID.randomUUID().toString,
      "Admin",
      permission = permissions.toList),
    UserRole(roleID = UUID.randomUUID().toString,
      "Basic User",
      permission =
        permissions.filter(_.permissionType == "View Only").toList),
    UserRole(roleID = UUID.randomUUID().toString,
      roleName = "Guardian",
      permission =
        permissions.filterNot(_.permissionType == "VerifierOn").toList)
  )

  val users: Seq[User] = Seq(
    User("Hiran", "Thenuwara", "hiranthenuwara"),
    User("Prageeth", "Gunathilaka", "prageethmahendra", roles = roles.toList),
    User("Lalin",
      "Thenuwara",
      "lalin",
      roles = roles.filter(_.roleName == "Guardian").toList),
    User("Disabled User", "disabled", "disabled", enable = false)
  )



}
