package io.sommers.zio.twitch
package model.webhook

import zio.json.{DeriveJsonDecoder, JsonDecoder, jsonField}
import zio.schema.annotation.fieldName

case class Message(
  text: String,
  fragments: List[MessageFragment]
)

object Message {
  implicit val jsonDecoder: JsonDecoder[Message] = DeriveJsonDecoder.gen[Message]
}

case class MessageFragment(
  @fieldName("type") @jsonField("type") fragmentType: String,
  cheermote: Option[Cheermote],
  emote: Option[Emote],
  mention: Option[Mention]
)

object MessageFragment {
  implicit val jsonDecoder: JsonDecoder[MessageFragment] = DeriveJsonDecoder.gen[MessageFragment]
}

final case class Cheermote(
  prefix: String,
  bits: Int,
  tier: Int
)

object Cheermote {
  implicit val jsonDecoder: JsonDecoder[Cheermote] = DeriveJsonDecoder.gen[Cheermote]
}

final case class Mention(
  userId: String,
  userLogin: String,
  userName: String
)

object Mention {
  implicit val jsonDecoder: JsonDecoder[Mention] = DeriveJsonDecoder.gen[Mention]
}

final case class Emote(
  id: String,
  emoteSetId: String,
  ownerId: String,
  format: List[String]
)

object Emote {
  implicit val jsonDecoder: JsonDecoder[Emote] = DeriveJsonDecoder.gen[Emote]
}