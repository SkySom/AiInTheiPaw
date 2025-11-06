package io.sommers.aiintheipaw
package command

import model.message.ReceivedMessage
import model.problem.Problem

import zio.IO

trait Command {
  val name: String

  val description: String

  val options: Array[CommandOption[?]] = Array()

  def run(message: ReceivedMessage, args: Map[String, AnyVal]): IO[Problem, Unit]
}