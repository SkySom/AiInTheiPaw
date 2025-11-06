package io.sommers.aiintheipaw
package util

import model.problem.{Problem, ThrowableProblem}

import zio.{Duration, Exit}

import java.sql.SQLException
import java.time.temporal.ChronoUnit

object CacheHelper {
  def handleTTL[A](exit: Exit[Problem, A]): Duration = {
    exit.foldExit(
      failed = cause => cause.failureOption match {
        case Some(ThrowableProblem(_: SQLException)) => Duration.Zero
        case _ => Duration(1, ChronoUnit.MINUTES)
      },
      completed = _ => Duration(10, ChronoUnit.MINUTES)
    )
  }
}
