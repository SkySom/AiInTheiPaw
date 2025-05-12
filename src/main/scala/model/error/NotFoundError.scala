package io.sommers.aiintheipaw
package model.error

import zio.schema.{DeriveSchema, Schema}

case class NotFoundError(
  objectType: String,
  message: Option[String] = None
) extends ProblemProvider {

  override def toProblem(instance: String): Problem = new Problem(
    None,
    404,
    message.getOrElse(s"$objectType was not found. "),
    None,
    instance,
    Map.empty
  )
}

object NotFoundError {
  implicit val schema: Schema[NotFoundError] = DeriveSchema.gen[NotFoundError]

  def apply(objectType: String, message: String): NotFoundError = NotFoundError(objectType, Some(message))
}
