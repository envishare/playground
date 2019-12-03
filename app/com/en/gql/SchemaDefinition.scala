package com.en.gql

import akka.http.scaladsl.model.headers.ContentDispositionTypes.attachment
import com.en.model._
import sangria.execution.deferred.{Fetcher, HasId}
import sangria.schema.{Argument, BooleanType, EnumType, EnumValue, Field, ListType, LongType, ObjectType, OptionInputType, OptionType, Schema, StringType, fields}
import com.en.services.EnvishareService

import scala.concurrent.Future

/**
  * Defines a GraphQL schema for the current project
  */
object SchemaDefinition {

  /**
    * Resolves the lists of characters. These resolutions are batched and
    * cached for the duration of a query.
    */
  val allActivities: Fetcher[EnvishareService, Activity, Activity, String] =
    Fetcher.caching((ctx: EnvishareService, ids: Seq[String]) ⇒
      Future.successful(ids.flatMap(id ⇒ ctx.getActivities(ActivityStatus.PENDING))))(HasId(_.id))

  val StatusEnum = EnumType(
    "Status",
    Some("One of the films in the Star Wars Trilogy"),
    List(
      EnumValue("PENDING",
        value = ActivityStatus.PENDING,
        description = Some(
          "Activity is in PENDING status, and waiting for VERIFIED.")),
      EnumValue("VERIFIED",
        value = ActivityStatus.VERIFIED,
        description =
          Some("Activity verification is completed, and published")),
      EnumValue("DELETED",
        value = ActivityStatus.DELETED,
        description = Some("Activity is deleted and unpublished."))
    )
  )

  val ActivityReviewInterface: ObjectType[EnvishareService, ActivityReview] =
    ObjectType(
      "ActivityReview",
      "Activity Review",
      () ⇒
        fields[EnvishareService, ActivityReview](
          Field("id",
            LongType,
            Some("The id of the review."),
            resolve = _.value.id),
          Field("review",
            OptionType(StringType),
            Some("review details."),
            resolve = _.value.review)
        )
    )

  implicit val PermissionInterface: ObjectType[EnvishareService, Permission] =
    ObjectType(
      "PermissionType",
      "Permission Type",
      () ⇒
        fields[EnvishareService, Permission](
          Field("permisionType",
            StringType,
            Some("The Permission Type."),
            resolve = _.value.permissionType),
          Field("description",
            StringType,
            Some("The description of the permission."),
            resolve = _.value.description)
        )
    )

  implicit val UserRoleInterface: ObjectType[EnvishareService, UserRole] =
    ObjectType(
      "UserRole",
      "User Role",
      () ⇒
        fields[EnvishareService, UserRole](
          Field("roleId",
            StringType,
            Some("The id of the role."),
            resolve = _.value.roleID),
          Field("roleName",
            StringType,
            Some("The name of the role."),
            resolve = _.value.roleName),
          Field("permission",
            ListType(PermissionInterface),
            Some("The name of the role."),
            resolve = _.value.permission)
        )
    )

  implicit val UserInterface: ObjectType[EnvishareService, User] =
    ObjectType(
      "User",
      "User Profile",
      () ⇒
        fields[EnvishareService, User](
          Field("userId",
            StringType,
            Some("The id of the user."),
            resolve = _.value.userId),
          Field("name",
            StringType,
            Some("The display name of the user."),
            resolve = _.value.name),
          Field("lastName",
            StringType,
            Some("The last name of the user."),
            resolve = _.value.lastName),
          Field("userName",
            StringType,
            Some("The user name of the user."),
            resolve = _.value.userName),
          Field("userSecret",
            StringType,
            Some("The user name of the user."),
            resolve = _.value.userSecret),
          Field("createdAt",
            DateTimeGqlType.UtcDateTimeType,
            Some("The user created date and time."),
            resolve = _.value.createdAt),
          Field("createdBy",
            StringType,
            Some("The User who created this user."),
            resolve = _.value.createdBy),
          Field("enable",
            BooleanType,
            Some("true for active users and false for inactive users."),
            resolve = _.value.enable),
          Field("roles",
            ListType(UserRoleInterface),
            Some("true for active users and false for inactive users."),
            resolve = _.value.roles)
        )
    )

  implicit val ActivityInterface: ObjectType[EnvishareService, Activity] =
    ObjectType(
      "Activity",
      "A character in the Star Wars Trilogy",
      () ⇒
        fields[EnvishareService, Activity](
          Field("id",
            StringType,
            Some("The id of the activity."),
            resolve = _.value.id),
          Field("name",
            OptionType(StringType),
            Some("The name of the activity."),
            resolve = _.value.name),
          Field("content",
            OptionType(StringType),
            Some("The content of the activity."),
            resolve = _.value.content),
          Field("status",
            StatusEnum,
            Some("The status of the activity."),
            resolve = _.value.status),
          Field("createdAt",
            DateTimeGqlType.UtcDateTimeType,
            Some("The createdAt of the activity."),
            resolve = _.value.createdAt),
          Field("createdBy",
            OptionType(StringType),
            Some("The createdBy of the activity."),
            resolve = _.value.createdBy),
          Field("modifiedBy",
            OptionType(StringType),
            Some("The modifiedBy of the activity."),
            resolve = _.value.modifiedBy),
          Field("modifiedAt",
            OptionType(DateTimeGqlType.UtcDateTimeType),
            Some("The modifiedAt of the activity."),
            resolve = _.value.modifiedAt),
          Field("review",
            ListType(ActivityReviewInterface),
            Some("The reviews of the activity."),
            resolve = _.value.reviews),
          Field("attachments",
            ListType(StringType),
            Some("The attachments of the activity."),
            resolve = _.value.attachments)
        )
    )

  implicit val ActivityReferenceInterface
  : ObjectType[EnvishareService, ActivityReference] =
    ObjectType(
      "ActivityReference",
      "Activity Reference",
      () ⇒
        fields[EnvishareService, ActivityReference](
          Field("parent",
            OptionType(ActivityInterface),
            Some("parent activity."),
            resolve = _.value.parentActivity),
          Field("child",
            OptionType(ActivityInterface),
            Some("child activity"),
            resolve = _.value.childActivity)
        )
    )

  val Activities = ListType(ActivityInterface)
  val Users = ListType(UserInterface)
  val UserRoles = ListType(UserRoleInterface)

  val ID = Argument("id", StringType, description = "id of the activity")

  val ActivityStatusArg = Argument(
    "status",
    StatusEnum,
    description = "status of the requesting activities."
  )

  val ActiveUserArg = Argument(
    "enable",
    BooleanType,
    description = "true for active users and false for inactive users."
  )

  val activityNameArg = Argument("name", StringType, description = "name of the activity")
  val activityIdArg = Argument("id", StringType, description = "id of the activity")
  val activityAttachmentArg = Argument("attachment", StringType,
    description = "attachment of the activity")
  val userIdArg = Argument("userId", StringType, description = "user id of the user")
  val userPasswordArg = Argument("password", StringType, description = "user password of the user")
  val userCreatedByArg = Argument("userCreatedBy", StringType,
    description = "created user of the user")
  val userNameArg = Argument("userName", StringType, description = "userName of the user")
  val userFirstNameArg = Argument("firstName", StringType, description = "userName of the user")
  val userLastNameArg = Argument("lastName", StringType, description = "user last name of the user")
  val userSecretArg = Argument("userSecret", StringType, description = "secret of the user")
  val reviewIdArg = Argument("reviewId", StringType, description = "id of the activity review")
  val reviewDetailsArg = Argument("review", StringType,
    description = "content of the activity review")
  val activityContentArg = Argument("content", StringType,
    description = "content details of the activity")
  val activityCreatedByArg = Argument("createdBy", StringType,
    description = "id of the user who created the activity")
  val activityModifiedByArg = Argument("modifiedBy", StringType,
    description = "id of the user who last updated the activity")

  val Query = ObjectType(
    "Query",
    fields[EnvishareService, Unit](
      Field(
        "activities",
        Activities,
        arguments = ActivityStatusArg :: Nil,
        resolve = ctx ⇒ ctx.ctx.getActivities(ctx.arg(ActivityStatusArg))
      ),
      Field("activity",
        OptionType(ActivityInterface),
        arguments = ID :: Nil,
        resolve = ctx ⇒ ctx.ctx.getActivity(ctx arg ID)),
      Field("users",
        Users,
        arguments = ActiveUserArg :: Nil,
        resolve = ctx ⇒ ctx.ctx.getUsers(ctx arg ActiveUserArg)),
      Field("user",
        OptionType(UserInterface),
        arguments = userIdArg :: Nil,
        resolve = ctx ⇒ ctx.ctx.getUser(ctx arg userIdArg)),
      Field("userRoles",
        UserRoles,
        arguments = ActiveUserArg :: Nil,
        resolve = ctx ⇒ ctx.ctx.getRoles)
    )
  )

  val mutation: List[Field[EnvishareService, Unit]] =
    fields[EnvishareService, Unit](
      Field(
        name = "createActivity",
        arguments = List(
          activityNameArg,
          activityContentArg,
          activityCreatedByArg
        ),
        fieldType = ActivityInterface,
        resolve = c => c.ctx.createActivity(
          activityName = c.arg(activityNameArg),
          activityContent = c.arg(activityContentArg),
          activityCreatedBy = c.arg(activityCreatedByArg)
        )
      ),
      Field(
        name = "updateActivity",
        arguments = List(
          activityIdArg,
          activityNameArg,
          activityContentArg,
          ActivityStatusArg,
          activityModifiedByArg
        ),
        fieldType = ActivityInterface,
        resolve = c => c.ctx.updateActivity(
          activityId = c.arg(activityIdArg),
          activityName = c.arg(activityNameArg),
          activityContent = c.arg(activityContentArg),
          activityStatus = c.arg(ActivityStatusArg),
          activityModifiedBy = c.arg(activityModifiedByArg)
        )
      ),
      Field(
        name = "addReview",
        arguments = List(
          activityIdArg,
          reviewDetailsArg,
          activityModifiedByArg
        ),
        fieldType = ActivityInterface,
        resolve = c => c.ctx.addReview(
          activityId = c.arg(activityIdArg),
          reviewDetails = c.arg(reviewDetailsArg),
          activityModifiedBy = c.arg(activityModifiedByArg)
        )
      ),
      Field(
        name = "deleteActivity",
        arguments = List(
          activityIdArg
        ),
        fieldType = ActivityInterface,
        resolve = c => c.ctx.deleteActivity(
          activityId = c.arg(activityIdArg)
        )
      ),
      Field(
        name = "deleteReview",
        arguments = List(
          activityIdArg,
          reviewIdArg,
          activityModifiedByArg
        ),
        fieldType = ActivityInterface,
        resolve = c => c.ctx.deleteReview(
          activityId = c.arg(activityIdArg),
          reviewId = c.arg(reviewIdArg),
          modifiedBy = c.arg(activityModifiedByArg)
        )
      ),
      Field(
        name = "updateReview",
        arguments = List(
          activityIdArg,
          reviewIdArg,
          reviewDetailsArg,
          activityModifiedByArg
        ),
        fieldType = ActivityInterface,
        resolve = c => c.ctx.updateReview(
          activityId = c.arg(activityIdArg),
          reviewId = c.arg(reviewIdArg),
          reviewDetails = c.arg(reviewDetailsArg),
          activityModifiedBy = c.arg(activityModifiedByArg)
        )
      ),
      Field(
        name = "addActivityAttachment",
        arguments = List(
          activityIdArg,
          activityAttachmentArg,
          activityModifiedByArg
        ),
        fieldType = ActivityInterface,
        resolve = c => c.ctx.addActivityAttachment(
          activityId = c.arg(activityIdArg),
          attachment = c.arg(activityAttachmentArg),
          activityModifiedBy = c.arg(activityModifiedByArg)
        )
      ),
      Field(
        name = "deleteActivityAttachment",
        arguments = List(
          activityIdArg,
          activityAttachmentArg,
          activityModifiedByArg
        ),
        fieldType = ActivityInterface,
        resolve = c => c.ctx.deleteActivityAttachment(
          activityId = c.arg(activityIdArg),
          attachment = c.arg(activityAttachmentArg),
          activityModifiedBy = c.arg(activityModifiedByArg)
        )
      ),
      Field(
        name = "createUser",
        arguments = List(
          userIdArg,
          userFirstNameArg,
          userLastNameArg,
          userNameArg,
          userSecretArg,
          userCreatedByArg
        ),
        fieldType = UserInterface,
        resolve = c => c.ctx.createUser(
          userId = c.arg(userIdArg),
          firstName = c.arg(userFirstNameArg),
          userLastName = c.arg(userLastNameArg),
          userName = c.arg(userNameArg),
          userSecret = c.arg(userSecretArg),
          userCreatedBy = c.arg(userCreatedByArg)
        )
      )
    )
  private val Mutation = ObjectType(name = "Mutation", fields = mutation)
  val activitySchema: Schema[EnvishareService, Unit] = Schema(Query, Some(Mutation))
}
