package io.sommers.aiintheipaw
package model.problem

import zio.schema.{DeriveSchema, Schema}

case class NotFoundProblem(
  objectType: String,
  message: Option[String] = None
) extends Problem {

  override val problemType: ProblemType = ClientProblem
}

object NotFoundProblem {
  implicit val schema: Schema[NotFoundProblem] = DeriveSchema.gen[NotFoundProblem]

  def apply(objectType: String, message: String): NotFoundProblem = NotFoundProblem(objectType, Some(message))
}
