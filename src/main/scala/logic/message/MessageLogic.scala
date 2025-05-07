package io.sommers.aiintheipaw
package logic.message

import logic.ServiceSpecific
import model.channel.Channel
import model.message.Message

import scala.util.Try

trait MessageLogic extends ServiceSpecific {
  def sendMessage(channel: Channel, replyToId: Option[String], message: String): Try[Message]
}
