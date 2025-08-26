package io.sommers.aiintheipaw
package model.sprint

enum SprintStatus(val name: String, val allowSignUp: Boolean, val allowCounts: Boolean, val active: Boolean) {
  case SignUp extends SprintStatus("Sign Up", true, false, true)
  case InProgress extends SprintStatus("In Progress", true, true, true)
  case InProgressOvertime extends SprintStatus("In Progress (Overtime)", true, true, true)
  case AwaitingCounts extends SprintStatus("Awaiting Counts", false, true, true)
  case Complete extends SprintStatus("Complete", false, false, false)
  case Pause extends SprintStatus("Pause", true, true, true)
  case Cancelled extends SprintStatus("Cancelled", false, false, false)
  case Unknown extends SprintStatus("Unknown", false, false, false)
}