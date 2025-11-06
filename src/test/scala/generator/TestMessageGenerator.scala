package io.sommers.aiintheipaw
package generator

import model.channel.Channel
import model.message.{BasicReceivedMessage, ReceivedMessage}
import model.user.User

import zio.{UIO, ZIO}

object TestMessageGenerator {
  def generateMessage(channel: Channel, user: User, text: Option[String] = None): UIO[ReceivedMessage] = for {
    messageId <- ZIO.randomWith(_.nextString(8))
    message <- text.fold(ZIO.randomWith(_.nextString(16)))(ZIO.succeed(_))
  } yield BasicReceivedMessage(user, channel, messageId, message)
}
