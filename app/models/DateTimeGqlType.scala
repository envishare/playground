package models

import java.time.format.DateTimeParseException
import java.time.{Instant, LocalDateTime}

import sangria.schema._
import sangria.ast
import sangria.validation.ValueCoercionViolation

object DateTimeGqlType {
  val UtcDateTimeType: ScalarType[Instant] = ScalarType[Instant](
    name = "UtcDateTime",
    coerceOutput = { (d, _) =>
      d.toString
    },
    coerceUserInput = {
      case x: String => parseUtcDateTime(x)
      case _         => Left(UtcDateTimeCoercionViolation)
    },
    coerceInput = {
      case ast.StringValue(s, _, _, _, _) => parseUtcDateTime(s)
      case _                              => Left(UtcDateTimeCoercionViolation)
    }
  )

  private def parseUtcDateTime(
      s: String): Either[UtcDateTimeCoercionViolation.type, Instant] = {
    try {
      Right(Instant.parse(s))
    } catch {
      case _: DateTimeParseException => Left(UtcDateTimeCoercionViolation)
    }
  }
}

case object UtcDateTimeCoercionViolation
    extends ValueCoercionViolation("UtcDateTime expected")
