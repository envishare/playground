package models

import java.time.LocalDateTime

import models.SchemaDefinition.ActivityReferenceInterface
import sangria.execution.deferred.{Fetcher, HasId}
import sangria.schema._

import scala.concurrent.Future
import models._

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
      Future.successful(ids.flatMap(id ⇒ ctx.getActivities(None))))(HasId(_.id))

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
//          Field("status",
//            StatusEnum,
//            Some("The status of the activity."),
//            resolve = _.value.status),
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
                DateTimeGqlType.UtcDateTimeType,
                Some("The modifiedAt of the activity."),
                resolve = _.value.modifiedAt),
          Field("reference",
                ActivityReferenceInterface,
                Some("Activity Reference"),
                resolve = _.value.activityReference),
          Field("review",
                ListType(ActivityReviewInterface),
                Some("The reviews of the activity."),
                resolve = _.value.review)
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
    OptionInputType(StatusEnum),
    description = "status of the requesting activities."
  )

  val ActiveUserArg = Argument(
    "enable",
    BooleanType,
    description = "true for active users and false for inactive users."
  )

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
      Field("userRoles",
            UserRoles,
            arguments = ActiveUserArg :: Nil,
            resolve = ctx ⇒ ctx.ctx.getRoles)
    )
  )

  val StarWarsSchema = Schema(Query)
}
