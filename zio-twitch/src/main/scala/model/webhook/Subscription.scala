package io.sommers.zio.twitch
package model.webhook

import zio.json.{DeriveJsonDecoder, JsonDecoder}
import zio.schema.{DeriveSchema, Schema}
import zio.schema.annotation.fieldName

import java.net.URL
import java.time.Instant


case class Subscription(
  id: String,
  status: String,
  @fieldName("type") eventType: String,
  version: String,
  cost: Int,
  condition: Map[String, String],
  transport: Transport,
  @fieldName("created_at") createdAt: Instant
)

object Subscription {
  implicit val schema: Schema[Subscription] = DeriveSchema.gen[Subscription]
  implicit val jsonDecoder: JsonDecoder[Subscription] = DeriveJsonDecoder.gen[Subscription]
}

case class Transport(
  method: String,
  callback: URL
)

object Transport {
  implicit val schema: Schema[Transport] = DeriveSchema.gen[Transport]
  implicit val jsonDecoder: JsonDecoder[Subscription] = DeriveJsonDecoder.gen[Subscription]
}
