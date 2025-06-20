package io.sommers.zio.twitch
package model.webhook

import zio.json.{DeriveJsonDecoder, JsonDecoder, SnakeCase, jsonField, jsonMemberNames}
import zio.schema.annotation.fieldName

import scala.annotation.unused

case class Message(
  text: String,
  fragments: List[MessageFragment]
)

object Message {
  implicit val jsonDecoder: JsonDecoder[Message] = DeriveJsonDecoder.gen[Message]
}

@jsonMemberNames(SnakeCase)
case class MessageFragment(
  @fieldName("type") @jsonField("type") fragmentType: String,
  cheermote: Option[Cheermote],
  emote: Option[Emote],
  mention: Option[Mention]
)

@unused
object MessageFragment {
  implicit val jsonDecoder: JsonDecoder[MessageFragment] = DeriveJsonDecoder.gen[MessageFragment]
}

final case class Cheermote(
  prefix: String,
  bits: Int,
  tier: Int
)

@unused
object Cheermote {
  implicit val jsonDecoder: JsonDecoder[Cheermote] = DeriveJsonDecoder.gen[Cheermote]
}

@jsonMemberNames(SnakeCase)
final case class Mention(
  userId: String,
  userLogin: String,
  userName: String
)

@unused
object Mention {
  implicit val jsonDecoder: JsonDecoder[Mention] = DeriveJsonDecoder.gen[Mention]
}

@jsonMemberNames(SnakeCase)
final case class Emote(
  id: String,
  emoteSetId: String,
  ownerId: String,
  format: List[String]
)

@unused
object Emote {
  implicit val jsonDecoder: JsonDecoder[Emote] = DeriveJsonDecoder.gen[Emote]
}