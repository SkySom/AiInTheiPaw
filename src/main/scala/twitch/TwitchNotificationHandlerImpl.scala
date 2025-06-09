package io.sommers.aiintheipaw
package twitch

import logic.ChannelLogic
import model.problem.Problem
import model.service.TwitchService

import io.sommers.zio.twitch.model.webhook.Subscription
import io.sommers.zio.twitch.model.webhook.event.ChannelChatMessage
import io.sommers.zio.twitch.server.TwitchNotificationHandler
import zio.{IO, ZIO, ZLayer}

case class TwitchNotificationHandlerImpl(
  channelLogic: ChannelLogic
) extends TwitchNotificationHandler {

  override def handleNotification[TE](subscription: Subscription, event: TE): IO[Throwable, Unit] = (for {
    _ <- ZIO.whenCase(event) {
      case channelChatMessage: ChannelChatMessage => for {
        channel <- channelLogic.findChannelForService(TwitchService, channelChatMessage.broadcasterUserId)
        _ <- ZIO.log(s"Channel: $channel")
        _ <- handleChatMessage(channelChatMessage)
      } yield ()
    }
  } yield ()).foldZIO(error => ZIO.log(s"Found Problem: ${error.toString}"), _ => ZIO.succeed(()))

  private def handleChatMessage(channelChatMessage: ChannelChatMessage): IO[Problem, Unit] = {
    ZIO.log(channelChatMessage.message.text)
  }
}

object TwitchNotificationHandlerImpl {
  val layer: ZLayer[ChannelLogic, Nothing, TwitchNotificationHandler] = ZLayer.fromFunction(TwitchNotificationHandlerImpl(_))
}