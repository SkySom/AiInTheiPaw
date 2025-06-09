package io.sommers.zio.twitch
package model.webhook

import zio.json.{DeriveJsonDecoder, JsonDecoder}

case class Cheer(
  bits: Int
)

object Cheer {
  implicit val jsonDecoder: JsonDecoder[Cheer] = DeriveJsonDecoder.gen[Cheer]
}
