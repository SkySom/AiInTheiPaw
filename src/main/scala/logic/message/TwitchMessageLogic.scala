package io.sommers.aiintheipaw
package logic.message

import http.exception.ServiceCallException
import model.channel.Channel
import model.message.Message
import model.service.{Service, TwitchService}

import io.sommers.zio.twitch.client.TwitchRestClient
import zio.{IO, ZLayer}

case class TwitchMessageLogic(
  twitchRestClient: TwitchRestClient
) extends MessageLogic {

  override def sendMessage(channel: Channel, replyToId: Option[String], message: String): IO[Throwable, Message] = {
    for {
      send <- twitchRestClient.sendMessage("1200371172", replyToId, message)
    } yield new Message {
      override def getText: String = send.messageId
    }
  }.mapError(twitchClientError => new ServiceCallException(twitchClientError.message, TwitchService, twitchClientError.exception))

  override val service: Service = TwitchService
}

object TwitchMessageLogic {
  val live: ZLayer[TwitchRestClient, Nothing, TwitchMessageLogic] = ZLayer.fromFunction(TwitchMessageLogic(_))
}
