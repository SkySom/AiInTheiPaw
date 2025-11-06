package io.sommers.aiintheipaw
package model.sprint

import model.problem.{InvalidStateProblem, Problem}

import zio.json.{JsonDecoder, JsonEncoder}
import zio.{Duration, IO, ZIO}

enum SprintStatus(val name: String, val allowSignUp: Boolean, val allowCounts: Boolean, val active: Boolean)
  derives JsonEncoder, JsonDecoder {
  case SignUp extends SprintStatus("Sign Up", true, false, true)
  case InProgress extends SprintStatus("In Progress", true, true, true)
  case InProgressOvertime extends SprintStatus("In Progress (Overtime)", true, true, true)
  case AwaitingCounts extends SprintStatus("Awaiting Counts", false, true, true)
  case Complete extends SprintStatus("Complete", false, false, false)
  case Pause extends SprintStatus("Pause", true, true, true)
  case Cancelled extends SprintStatus("Cancelled", false, false, false)
  case Unknown extends SprintStatus("Unknown", false, false, false)

  def nextStatus(): IO[Problem, SprintStatus] = {
    this match {
      case InProgress => ZIO.succeed(AwaitingCounts)
      case AwaitingCounts => ZIO.succeed(Complete)
      case _ => ZIO.fail(InvalidStateProblem(s"No next status for ${this.name}"))
    }
  }
  
  def getDuration(signUp: Duration, inProgress: Duration, awaitingCounts: Duration): IO[Problem, Duration] = {
    this match {
      case SignUp => ZIO.succeed(signUp)
      case InProgress => ZIO.succeed(inProgress)
      case AwaitingCounts => ZIO.succeed(awaitingCounts)
      case _ => ZIO.fail(InvalidStateProblem(s"No duration for ${this.name}"))
    }
  }
}