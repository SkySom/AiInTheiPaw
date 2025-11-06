package io.sommers.zio.twitch
package model.webhook

import zio.json.{DeriveJsonDecoder, JsonDecoder, SnakeCase, jsonMemberNames}

@jsonMemberNames(SnakeCase)
case class Reply(
  parentMessageId: String,
  parentMessageBody: String,
  parentUserId: String,
  parentUserName: String,
  parentUserLogin: String,
  threadMessageId: String,
  threadUserId: String,
  threadUserName: String,
  threadUserLogin: String
)

object Reply {
  implicit val jsonDecoder: JsonDecoder[Reply] = DeriveJsonDecoder.gen[Reply]
}
