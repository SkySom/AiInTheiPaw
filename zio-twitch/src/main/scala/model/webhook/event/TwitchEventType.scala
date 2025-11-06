package io.sommers.zio.twitch
package model.webhook.event

import zio.{IO, ZIO}
import zio.json.JsonDecoder
import zio.json.ast.Json

sealed trait TwitchEventType[ET <: TwitchEvent[ET]] {
  val name: String
  val jsonDecoder: JsonDecoder[ET]
}

object TwitchEventType {
  def parse(typeName: String, json: Json): IO[String, TwitchEvent[?]] = for {
    eventTypeOpt <- ZIO.succeed(typeName match {
      case ChannelChatMessageEventType.name => json.as[ChannelChatMessage]
      case _ => Left(s"No JsonDecoder for $typeName")
    })
    event <- ZIO.fromEither(eventTypeOpt)
  } yield event
}

object ChannelChatMessageEventType extends TwitchEventType[ChannelChatMessage] {
  override val name: String = "channel.chat.message"
  override val jsonDecoder: JsonDecoder[ChannelChatMessage] = ChannelChatMessage.jsonDecoder
}