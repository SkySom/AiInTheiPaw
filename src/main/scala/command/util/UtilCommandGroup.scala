package io.sommers.aiintheipaw
package command.util

import command.{Command, CommandGroup}

import io.sommers.aiintheipaw.logic.message.MessageLogic
import zio.{URLayer, ZLayer}

case class UtilCommandGroup(
  whoAmICommand: WhoAmICommand
) extends CommandGroup {
  override val name: String = "utils"

  override val commands: Seq[Command] = Seq(
    whoAmICommand
  )
}

object UtilCommandGroup {
  val layer: URLayer[WhoAmICommand, UtilCommandGroup] = ZLayer.fromFunction(UtilCommandGroup(_))
  
  val fullLayer: URLayer[MessageLogic, UtilCommandGroup] = WhoAmICommand.layer >>> layer
}