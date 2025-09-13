package io.sommers.aiintheipaw
package model.problem

import zio.ZIO

trait Problem {
  val problemType: ProblemType
  
  def message: String
}

object Problem {
  def apply(any: Any): Problem = any match {
    case throwable: Throwable => ThrowableProblem(throwable)
    case problem: Problem => problem
    case other => ThrowableProblem(new IllegalArgumentException(s"Unable to convert $other into Problem"))
  }
  
  def applyZIO[IN, OUT](any: Any): ZIO[IN, Problem, OUT] = ZIO.fail(Problem(any))
}
