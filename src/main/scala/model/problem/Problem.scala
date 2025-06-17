package io.sommers.aiintheipaw
package model.problem

trait Problem {
  val problemType: ProblemType
}

object Problem {
  def apply(any: Any): Problem = any match {
    case throwable: Throwable => ThrowableProblem(throwable)
    case problem: Problem => problem
    case other => ThrowableProblem(new IllegalArgumentException(s"Unable to convert $other into Problem"))
  }
}
