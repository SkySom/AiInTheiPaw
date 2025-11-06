package io.sommers.aiintheipaw
package logic.message

import model.channel.Channel
import model.message.{BotMessage, Message}
import model.problem.{Problem, ServiceProblem}
import model.service.Service
import model.service.Service.Twitch

import io.sommers.zio.twitch.client.TwitchRestClient
import zio.{IO, URLayer, ZLayer}

case class TwitchServiceMessageLogic(
  twitchRestClient: TwitchRestClient
) extends ServiceMessageLogic {

  override def sendMessage(channel: Channel, replyToId: Option[String], message: String): IO[Problem, Message] = {
    for {
      _ <- twitchRestClient.sendMessage(channel.channelId, replyToId, message)
    } yield BotMessage(message)
  }.mapError(twitchClientError => ServiceProblem(twitchClientError.message, Twitch, twitchClientError.exception))

  override val service: Service = Twitch
}

object TwitchServiceMessageLogic {
  val live: URLayer[TwitchRestClient, TwitchServiceMessageLogic] = ZLayer.fromFunction(TwitchServiceMessageLogic(_))
}
