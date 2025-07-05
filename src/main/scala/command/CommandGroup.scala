package io.sommers.aiintheipaw
package command

trait CommandGroup {
  val name: String
  val commands: Seq[Command]
  
  def findCommand(name: String): Option[Command] = this.commandMap.get(name)
  
  private lazy val commandMap: Map[String, Command] = this.commands.map(command => {
    val commandShortName = if (command.name.endsWith(this.name)) {
      command.name.substring(0, command.name.lastIndexOfSlice(this.name))
    } else {
      command.name
    }
    (commandShortName, command)
  }).toMap
  
  
}
