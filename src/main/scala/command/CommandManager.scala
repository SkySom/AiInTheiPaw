package io.sommers.aiintheipaw
package command

import command.sprint.SprintCommandGroup
import command.util.UtilCommandGroup
import logic.SprintLogic
import logic.message.MessageLogic

import zio.{URLayer, ZLayer}

trait CommandManager {
  def commandGroups: Seq[CommandGroup]

  def commands: Seq[Command] = commandGroups.flatMap(_.commands)

  def getCommandByName(name: String): Option[Command]

  def getCommandByPath(path: String*): Option[Command]
}

object CommandManager {
  val live: URLayer[Seq[CommandGroup], CommandManagerImpl] = ZLayer.fromFunction(CommandManagerImpl(_))

  //noinspection ScalaWeakerAccess
  val allCommandLayer: URLayer[SprintLogic & MessageLogic, Seq[CommandGroup]] = ZLayer.collectAll(
    Seq(
      SprintCommandGroup.fullLayer,
      UtilCommandGroup.fullLayer
    )
  )

  val fullLive: URLayer[SprintLogic & MessageLogic, CommandManagerImpl] = allCommandLayer >>> live
}

case class CommandManagerImpl(
  commandGroups: Seq[CommandGroup]
) extends CommandManager {
  private lazy val commandGroupMap: Map[String, CommandGroup] = this.commandGroups.map(commandGroup => (commandGroup.name, commandGroup)).toMap

  override def getCommandByName(name: String): Option[Command] = this.commands.find(_.name == name)

  override def getCommandByPath(path: String*): Option[Command] = path match {
    case Seq(single) => getCommandByName(single)
    case Seq(commandGroup, command) => this.commandGroupMap.get(commandGroup)
      .flatMap(_.findCommand(command))
    case _ => None
  }
}

