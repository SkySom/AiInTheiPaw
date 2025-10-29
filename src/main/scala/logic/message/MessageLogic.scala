package io.sommers.aiintheipaw
package logic.message

import logic.{ServiceManager, ServiceManagerImpl, ServiceSpecific}
import model.channel.Channel
import model.message.{Message, ReceivedMessage}
import model.problem.Problem

import zio.{IO, URLayer, ZLayer}

trait MessageLogic {
  def sendMessage(channel: Channel, message: String): IO[Problem, Message] =
    sendMessage(channel, None, message)

  def sendMessage(channel: Channel, replyToId: Option[String], message: String): IO[Problem, Message]

  def replyToMessage(receivedMessage: ReceivedMessage, message: String): IO[Problem, Message] =
    sendMessage(receivedMessage.channel, Some(receivedMessage.messageId), message)
}

object MessageLogic {
  //noinspection ScalaWeakerAccess
  val serviceManagerLive: ZLayer[TwitchServiceMessageLogic, Nothing, ServiceManager[ServiceMessageLogic]] =
    ZLayer.fromFunction((twitch: TwitchServiceMessageLogic) => ServiceManagerImpl[ServiceMessageLogic]("MessageLogic", Set(twitch)))

  val live: URLayer[ServiceManager[ServiceMessageLogic], MessageLogic] = ZLayer.fromFunction(MessageLogicLive(_))

  val fullLive: URLayer[TwitchServiceMessageLogic, MessageLogic] = serviceManagerLive >>> live
}

case class MessageLogicLive(
  serviceManager: ServiceManager[ServiceMessageLogic]
) extends MessageLogic {
  override def sendMessage(channel: Channel, replyToId: Option[String], message: String): IO[Problem, Message] = for {
    serviceMessageLogic <- serviceManager.get(channel.service)
    message <- serviceMessageLogic.sendMessage(channel, replyToId, message)
  } yield message
}

trait ServiceMessageLogic extends ServiceSpecific {
  def sendMessage(channel: Channel, replyToId: Option[String], message: String): IO[Problem, Message]
}

