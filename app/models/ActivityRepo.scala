package models

import scala.util.Random

object ActivityRepo {
  val activities = List(
    Activity(
      id = "1000",
      name = Some("Lake Cleanup"),
      review = Seq(
        ActivityReview(
          id = Random.nextLong(),
          review = Random.nextString(20)
        )
      ).toList,
      activityReference = ActivityReference(
        parentActivity = None,
        childActivity =
          Some(Activity(id = "1001", name = Some("Ocean Cleanup"))))
    ),
    Activity(
      id = "1001",
      name = Some("Ocean Cleanup"),
      review = Seq(
        ActivityReview(
          id = Random.nextLong(),
          review = Random.nextString(20)
        )
      ).toList,
      activityReference = ActivityReference(
        parentActivity =
          Some(Activity(id = "1000", name = Some("Lake Cleanup"))),
        childActivity =
          Some(Activity(id = "1002", name = Some("Asvaddumization"))))
    ),
    Activity(
      id = "1002",
      name = Some("Asvaddumization"),
      review = Seq(
        ActivityReview(
          id = Random.nextLong(),
          review = Random.nextString(20)
        )
      ).toList,
      activityReference = ActivityReference(
        parentActivity =
          Some(Activity(id = "1001", name = Some("Ocean Cleanup"))),
        childActivity = Some(Activity(id = "1001", name = Some("Plant Trees"))))
    ),
    Activity(
      id = "1003",
      name = Some("Plant Trees"),
      review = Seq(
        ActivityReview(
          id = Random.nextLong(),
          review = Random.nextString(20)
        )
      ).toList,
      activityReference =
        ActivityReference(
          parentActivity =
            Some(Activity(id = "1002", name = Some("Asvaddumization"))),
          childActivity =
            Some(Activity(id = "1001", name = Some("Road Cleanup"))))
    ),
    Activity(
      id = "1004",
      name = Some("Road Cleanup"),
      review = Seq(
        ActivityReview(
          id = Random.nextLong(),
          review = Random.nextString(20)
        )
      ).toList,
      activityReference =
        ActivityReference(
          parentActivity =
            Some(Activity(id = "1003", name = Some("Plant Trees"))),
          childActivity = None
        )
    )
  )
}
