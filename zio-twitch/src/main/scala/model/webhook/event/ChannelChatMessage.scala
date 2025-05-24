package io.sommers.zio.twitch
package model.webhook.event

import model.webhook.{Badge, Cheer, Message, Reply}

import zio.json.{DeriveJsonCodec, DeriveJsonDecoder, JsonCodec, JsonDecoder}

case class ChannelChatMessage(
  broadcasterUserId: String,
  broadcasterUserName: String,
  broadcasterUserLogin: String,
  chatterUserId: String,
  chatterUserName: String,
  chatterUserLogin: String,
  messageId: String,
  message: Message,
  messageType: String,
  badges: List[Badge],
  cheer: Option[Cheer],
  reply: Option[Reply]
) extends TwitchEvent[ChannelChatMessage] {
  override val twitchEventType: ChannelChatMessageEventType.type = ChannelChatMessageEventType
}

object ChannelChatMessage {
  implicit val jsonDecoder: JsonDecoder[ChannelChatMessage] = DeriveJsonDecoder.gen[ChannelChatMessage]
}
