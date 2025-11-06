package io.sommers.aiintheipaw
package model.problem

case class InvalidStateProblem(
  override val message: String
) extends Problem {
  override val problemType: ProblemType = ProblemType.ServerProblem
}
