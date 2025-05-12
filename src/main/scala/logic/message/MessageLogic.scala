package io.sommers.aiintheipaw
package logic.message

import logic.{ServiceManager, ServiceManagerImpl, ServiceSpecific}
import model.channel.Channel
import model.message.Message

import zio.{IO, ZLayer}

trait MessageLogic extends ServiceSpecific {
  def sendMessage(channel: Channel, replyToId: Option[String], message: String): IO[Throwable, Message]
}

object MessageLogic {
  val live: ZLayer[TwitchMessageLogic, Nothing, ServiceManager[MessageLogic]] =
    ZLayer.fromFunction((twitch: TwitchMessageLogic) => ServiceManagerImpl[MessageLogic]("MessageLogic", Set(twitch)))
}
