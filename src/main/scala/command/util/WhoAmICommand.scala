package io.sommers.aiintheipaw
package command.util

import command.{Command, CommandOption}
import logic.message.MessageLogic
import model.message.ReceivedMessage
import model.problem.Problem

import zio.{IO, URLayer, ZLayer}

case class WhoAmICommand(
  messageLogic: MessageLogic
) extends Command {
  override val name: String = "whoami"

  override val description: String = "Ask Ai your [User] Info"

  override def run(message: ReceivedMessage, args: Map[String, AnyVal]): IO[Problem, Unit] = for {
    _ <- messageLogic.replyToMessage(message, s"You are [User ${message.userSource.displayName}]")
  } yield ()
}

object WhoAmICommand {
  val layer: URLayer[MessageLogic, WhoAmICommand] = ZLayer.fromFunction(WhoAmICommand(_))
}