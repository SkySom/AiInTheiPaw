package io.sommers.aiintheipaw
package command.sprint

import command.{Command, CommandOption, DurationCommandOption}
import event.EventScheduler
import logic.{SprintCommandLogic, SprintLogic}
import logic.message.MessageLogic
import model.message.ReceivedMessage
import model.problem.Problem

import zio.{Duration, IO, URLayer, ZEnvironment, ZLayer}

import java.util.concurrent.TimeUnit

case class StartSprintCommand(
  sprintCommandLogic: SprintCommandLogic,
  messageLogic: MessageLogic,
  eventScheduler: EventScheduler
) extends Command {
  private val durationCommandOption = DurationCommandOption(
    "sprintDuration",
    "The amount of time for the Sprint to run either as a number (of minutes) or in format: <x>m<y>s"
  )

  override val name: String = "startSprint"
  override val description: String = "Starts a Writing Sprint"
  override val options: Array[CommandOption[?]] = Array(durationCommandOption)

  override def run(message: ReceivedMessage, args: Map[String, AnyVal]): IO[Problem, Unit] = {
    for {
      duration <- durationCommandOption.find(args)
      _ <- sprintCommandLogic.createSprint(message.channel, message.user, Some(message.messageId), duration)
        .provideEnvironment(ZEnvironment(eventScheduler))
    } yield ()
  }.mapError(Problem(_))
}

object StartSprintCommand {
  val live: URLayer[SprintCommandLogic & MessageLogic & EventScheduler, StartSprintCommand] = ZLayer.fromFunction(StartSprintCommand.apply)
}
