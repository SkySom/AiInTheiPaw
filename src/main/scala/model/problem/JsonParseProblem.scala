package io.sommers.aiintheipaw
package model.problem

case class JsonParseProblem(
  override val message: String
) extends Problem {
  override val problemType: ProblemType = ProblemType.ClientProblem
}
