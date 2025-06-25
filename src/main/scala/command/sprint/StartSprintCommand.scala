package io.sommers.aiintheipaw
package command.sprint

import command.{Command, CommandOption, DurationCommandOption}
import logic.SprintLogic
import logic.message.MessageLogic
import model.message.{Message, ReceivedMessage}
import model.problem.Problem

import zio.{IO, Task, URLayer, ZLayer}

import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration

case class StartSprintCommand(
  sprintLogic: SprintLogic,
  messageLogic: MessageLogic
) extends Command {
  private val durationCommandOption = DurationCommandOption(
    "sprintDuration",
    "The amount of time for the Sprint to run in format: x minutes y seconds"
  )

  override val name: String = "startSprint"
  override val description: String = "Starts a Writing Sprint"
  override val options: Array[CommandOption[?]] = Array(durationCommandOption)

  override def run(message: ReceivedMessage, args: Map[String, AnyVal]): IO[Problem, Unit] = {
    for {
      duration <- durationCommandOption.find(args)
      sprint <- sprintLogic.createSprint(message.channel, message.user, duration.getOrElse(Duration(1, TimeUnit.MINUTES)))
      _ <- messageLogic.sendMessage(message.channel, Some(message.messageId), "Sprint has entered Sign Up")
    } yield ()
  }.mapError(Problem(_))
}

object StartSprintCommand {
  val live: URLayer[SprintLogic & MessageLogic, StartSprintCommand] = ZLayer.fromFunction(StartSprintCommand(_, _))
}
