package io.sommers.aiintheipaw
package http.request

import zio.schema.{DeriveSchema, Schema}


case class SendMessageRequest(
  channelId: Long,
  replyToId: Option[String],
  message: String
)

object SendMessageRequest {
  implicit val schema: Schema[SendMessageRequest] = DeriveSchema.gen[SendMessageRequest]
}

case class SendMessageResponse(
  messageId: String
)

object SendMessageResponse {
  implicit val schema: Schema[SendMessageResponse] = DeriveSchema.gen[SendMessageResponse]
}
