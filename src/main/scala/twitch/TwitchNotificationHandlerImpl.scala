package io.sommers.aiintheipaw
package twitch

import command.CommandManager
import logic.{ChannelLogic, GuildLogic, UserLogic}
import model.problem.{Problem, ProblemException}
import model.service.Service.Twitch

import io.sommers.zio.twitch.model.webhook.Subscription
import io.sommers.zio.twitch.model.webhook.event.ChannelChatMessage
import io.sommers.zio.twitch.server.TwitchNotificationHandler
import zio.{IO, URLayer, ZIO, ZLayer}

case class TwitchNotificationHandlerImpl(
  channelLogic: ChannelLogic,
  userLogic: UserLogic,
  commandManager: CommandManager,
  guildLogic: GuildLogic
) extends TwitchNotificationHandler {

  override def handleNotification[TE](subscription: Subscription, event: TE): IO[Throwable, Unit] =
    ZIO.whenCase[Any, Problem, TE, Unit](event) {
      case channelChatMessage: ChannelChatMessage => for {
        guild <- guildLogic.findGuildForService(Twitch, channelChatMessage.broadcasterUserId, channelChatMessage.broadcasterUserName)
        channel <- channelLogic.findChannelForService(Twitch, channelChatMessage.broadcasterUserId, guild, channelChatMessage.broadcasterUserName)
        user <- userLogic.findUserForService(Twitch, channelChatMessage.chatterUserId, channelChatMessage.chatterUserName)
        _ <- ZIO.log(s"Channel: $channel, User: $user")
        _ <- handleChatMessage(channelChatMessage)
      } yield ()
    }.mapBoth[Throwable, Unit](ProblemException(_), _ => ())

  private def handleChatMessage(channelChatMessage: ChannelChatMessage): IO[Problem, Unit] = {
    ZIO.log(channelChatMessage.message.text)
  }
}

object TwitchNotificationHandlerImpl {
  val layer: URLayer[ChannelLogic & UserLogic & CommandManager & GuildLogic, TwitchNotificationHandler] =
    ZLayer.fromFunction(TwitchNotificationHandlerImpl.apply)
}