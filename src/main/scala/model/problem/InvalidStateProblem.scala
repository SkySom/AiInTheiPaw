package io.sommers.aiintheipaw
package model.problem

case class InvalidStateProblem(
  message: String
) extends Problem {
  override val problemType: ProblemType = ProblemType.ServerProblem
}
