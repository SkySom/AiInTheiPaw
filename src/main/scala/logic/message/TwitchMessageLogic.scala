package io.sommers.aiintheipaw
package logic.message

import model.channel.Channel
import model.message.Message
import model.service.{Service, TwitchService}
import twitch.TwitchRestClient

import zio.{&, IO, ZLayer}

class TwitchMessageLogic(
  twitch: Service,
  twitchRestClient: TwitchRestClient
) extends MessageLogic {

  override def sendMessage(channel: Channel, replyToId: Option[String], message: String): IO[Throwable, Message] = {
    for {
      send <- twitchRestClient.sendMessage("1200371172", replyToId, message)
    } yield new Message {
      override def getText: String = send.messageId
    }
  }

  override val service: Service = twitch
}

object TwitchMessageLogic {
  val live: ZLayer[TwitchService & TwitchRestClient, Nothing, TwitchMessageLogic] = ZLayer.fromFunction(new TwitchMessageLogic(_, _))
}
