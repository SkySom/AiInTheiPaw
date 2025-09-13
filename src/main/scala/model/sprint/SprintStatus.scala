package io.sommers.aiintheipaw
package model.sprint

import io.sommers.aiintheipaw.model.problem.{InvalidStateProblem, Problem}
import zio.{IO, ZIO}
import zio.json.{JsonDecoder, JsonEncoder}

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
}