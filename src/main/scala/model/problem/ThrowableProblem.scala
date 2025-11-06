package io.sommers.aiintheipaw
package model.problem

import model.problem.ProblemType.ServerProblem

import zio.ZIO

case class ThrowableProblem(
  throwable: Throwable
) extends Problem {
  override val problemType: ProblemType = ServerProblem

  override def message: String = throwable.getMessage
}

object ThrowableProblem {
  def applyZIO[IN, OUT](throwable: Throwable): ZIO[IN, Problem, OUT] = ZIO.fail(ThrowableProblem(throwable))
}
