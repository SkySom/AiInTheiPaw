package io.sommers.aiintheipaw
package model.problem

case class InvalidValueProblem(
  message: String
) extends Problem {
  override val problemType: ProblemType = ProblemType.ClientProblem
}
