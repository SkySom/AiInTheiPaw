package io.sommers.aiintheipaw
package command

import model.message.Message

import zio.Task

trait Command {
  val name: String
  
  val description: String
  
  val options: Array[CommandOption[?]]
  
  def run(message: Message, args: Map[String, AnyVal]): Task[Unit]
}