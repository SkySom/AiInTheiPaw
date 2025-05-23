package io.sommers.zio.twitch
package model.webhook

import zio.schema.annotation.fieldName

case class Message(
  text: String,
  fragments: List[MessageFragment]
)

case class MessageFragment(
  @fieldName("type") fragmentType: String,
  cheermote: Option[Cheermote],
  emote: Option[Emote],
  mention: Option[Mention]
)


final case class Cheermote(
  prefix: String,
  bits: Int,
  tier: Int
)

final case class Mention(
  userId: String,
  userLogin: String,
  userName: String
)

final case class Emote(
  id: String,
  emoteSetId: String,
  ownerId: String,
  format: List[String]
)