package io.sommers.zio.twitch
package model.webhook.event

import zio.{IO, ZIO}
import zio.json.JsonDecoder

sealed trait TwitchEventType[ET <: TwitchEvent[ET]] {
  val name: String
  val jsonDecoder: JsonDecoder[ET]
}

object TwitchEventType {
  def get[ET <: TwitchEvent[ET]](typeName: String): IO[Throwable, TwitchEventType[ET]] = for {
    eventTypeOpt <- ZIO.whenCase[Any, Throwable, String, TwitchEventType[_]](typeName) {
      case ChannelChatMessageEventType.name => ZIO.succeed(ChannelChatMessageEventType)
      case _ => ZIO.fail(new IllegalArgumentException("Invalid Type"))
    }
    eventType <- ZIO.fromOption(eventTypeOpt)
      .mapError(_ => new IllegalArgumentException("Invalid Type"))
  } yield eventType.asInstanceOf
}

object ChannelChatMessageEventType extends TwitchEventType[ChannelChatMessage] {
  override val name: String = "channel.chat.message"
  override val jsonDecoder: JsonDecoder[ChannelChatMessage] = ChannelChatMessage.jsonDecoder
}