package io.sommers.zio.twitch
package model.webhook

import zio.json.{DeriveJsonDecoder, JsonDecoder, SnakeCase, jsonMemberNames}

@jsonMemberNames(SnakeCase)
case class Badge(
  setId: String,
  id: String,
  info: String
)

object Badge {
  implicit val jsonDecoder: JsonDecoder[Badge] = DeriveJsonDecoder.gen[Badge]
}
