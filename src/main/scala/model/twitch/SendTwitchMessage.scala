package io.sommers.aiintheipaw
package model.twitch

import zio.json.jsonMemberNames
import zio.json.ziojson_03.SnakeCase
import zio.schema.{DeriveSchema, Schema}

@jsonMemberNames(SnakeCase)
case class SendTwitchMessageRequest(
  broadcasterId: String,
  senderId: String,
  replyParentMessageId: Option[String],
  message: String
)

object SendTwitchMessageRequest {
  implicit val schema: Schema[SendTwitchMessageRequest] = DeriveSchema.gen[SendTwitchMessageRequest]
}

@jsonMemberNames(SnakeCase)
case class SendTwitchMessageResponse(
  messageId: String,
  isSent: Boolean,
  dropReason: Option[DropReason]
)

object SendTwitchMessageResponse {
  implicit val schema: Schema[SendTwitchMessageResponse] = DeriveSchema.gen[SendTwitchMessageResponse]
  implicit val dataSchema: Schema[DataResponse[SendTwitchMessageResponse]] = DeriveSchema.gen[DataResponse[SendTwitchMessageResponse]]
}

case class DropReason(
  code: String,
  message: String
)