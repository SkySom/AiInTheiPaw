package io.sommers.zio.twitch
package model.webhook

import zio.schema.{DeriveSchema, Schema}

case class WebhookRequest(
  subscription: Subscription,
  challenge: Option[String],
  event: Option[Map[String, String]]
) {

}

object WebhookRequest {
  implicit val schema: Schema[WebhookRequest] = DeriveSchema.gen[WebhookRequest]
}
