package io.sommers.aiintheipaw
package model.sprint

enum SprintStatus(name: String, allowSignUp: Boolean, allowCounts: Boolean) {
  case SignUp extends SprintStatus("Sign Up", true, false)
  case InProgress extends SprintStatus("In Progress", true, true)
  case InProgressOvertime extends SprintStatus("In Progress (Overtime)", true, true)
  case AwaitingCounts extends SprintStatus("Awaiting Counts", false, true)
  case Complete extends SprintStatus("Complete", false, false)
  case Pause extends SprintStatus("Pause", true, true)
  case Unknown extends SprintStatus("Unknown", false, false)
}