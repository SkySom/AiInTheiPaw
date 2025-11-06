package io.sommers.aiintheipaw
package model.problem

import model.problem.ProblemType.ClientProblem

import zio.schema.{DeriveSchema, Schema}

case class NotFoundProblem(
  objectType: String,
  override val message: String
) extends Problem {
  override val problemType: ProblemType = ClientProblem
}

object NotFoundProblem {
  implicit val schema: Schema[NotFoundProblem] = DeriveSchema.gen[NotFoundProblem]
}
