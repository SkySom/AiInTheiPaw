package io.sommers.aiintheipaw
package command.sprint

import command.{Command, CommandGroup}
import event.EventScheduler
import logic.message.MessageLogic
import logic.{SprintCommandLogic, SprintLogic}

import zio.{URLayer, ZLayer}

case class SprintCommandGroup(
  startSprintCommand: StartSprintCommand
) extends CommandGroup {
  override val name: String = "sprint"

  override val commands: Seq[Command] = Seq(
    startSprintCommand
  )
}

object SprintCommandGroup {
  //noinspection ScalaWeakerAccess
  def layer: URLayer[StartSprintCommand, SprintCommandGroup] = ZLayer.fromFunction(SprintCommandGroup(_))

  def fullLayer: URLayer[SprintCommandLogic & MessageLogic & EventScheduler, SprintCommandGroup] = StartSprintCommand.live >>> layer
}
