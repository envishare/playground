package com.en.model

import java.time.{Instant, LocalDateTime}
import java.util.UUID

case class User(
    name: String,
    lastName: String,
    userName: String,
    userId: String = UUID.randomUUID().toString,
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
