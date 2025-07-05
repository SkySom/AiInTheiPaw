package io.sommers.aiintheipaw
package model.problem

import model.problem.ProblemType.ServerProblem
import model.service.Service

case class ServiceProblem(
  message: String,
  service: Service,
  cause: Option[Throwable] = None
) extends Problem {
  override val problemType: ProblemType = ServerProblem
}
