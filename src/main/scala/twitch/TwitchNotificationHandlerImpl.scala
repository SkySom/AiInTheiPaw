package io.sommers.aiintheipaw
package twitch

import logic.{ChannelLogic, UserLogic}
import model.problem.Problem
import model.service.Service.Twitch

import io.sommers.zio.twitch.model.webhook.Subscription
import io.sommers.zio.twitch.model.webhook.event.ChannelChatMessage
import io.sommers.zio.twitch.server.TwitchNotificationHandler
import zio.{IO, URLayer, ZIO, ZLayer}

case class TwitchNotificationHandlerImpl(
  channelLogic: ChannelLogic,
  userLogic: UserLogic
) extends TwitchNotificationHandler {

  override def handleNotification[TE](subscription: Subscription, event: TE): IO[Throwable, Unit] = (for {
    _ <- ZIO.whenCase(event) {
      case channelChatMessage: ChannelChatMessage => for {
        channel <- channelLogic.findChannelForService(Twitch, channelChatMessage.broadcasterUserId)
        user <- userLogic.findUserForService(Twitch, channelChatMessage.chatterUserId, channelChatMessage.broadcasterUserName)
        _ <- ZIO.log(s"Channel: $channel, User: $user")
        _ <- handleChatMessage(channelChatMessage)
      } yield ()
    }
  } yield ()).foldZIO(error => ZIO.log(s"Found Problem: ${error.toString}"), _ => ZIO.succeed(()))

  private def handleChatMessage(channelChatMessage: ChannelChatMessage): IO[Problem, Unit] = {
    ZIO.log(channelChatMessage.message.text)
  }
}

object TwitchNotificationHandlerImpl {
  val layer: URLayer[ChannelLogic & UserLogic, TwitchNotificationHandler] = ZLayer.fromFunction(TwitchNotificationHandlerImpl(_, _))
}