package io.sommers.zio.twitch
package model.webhook

import model.webhook.event.{ChannelChatMessage, WebhookEvent}

import zio.schema.{DeriveSchema, Schema}

case class WebhookRequest[E <: WebhookEvent](
  subscription: Subscription,
  challenge: Option[String],
  event: E
) {

}

object WebhookRequest {
  implicit val channelChatMessageSchema: Schema[WebhookRequest[ChannelChatMessage]] = DeriveSchema.gen[WebhookRequest[ChannelChatMessage]]
}
