package io.sommers.zio.twitch
package model.webhook

import zio.json.{DeriveJsonDecoder, JsonDecoder, jsonField}
import zio.schema.annotation.fieldName
import zio.schema.{DeriveSchema, Schema}

import java.time.Instant
import scala.annotation.unused


case class Subscription(
  id: String,
  status: String,
  @fieldName("type") @jsonField("type") eventType: String,
  version: String,
  cost: Int,
  condition: Map[String, String],
  transport: Transport,
  @fieldName("created_at") @jsonField("created_at") createdAt: Instant
)

object Subscription {
  implicit val schema: Schema[Subscription] = DeriveSchema.gen[Subscription]
  implicit val jsonDecoder: JsonDecoder[Subscription] = DeriveJsonDecoder.gen[Subscription]
}

case class Transport(
  method: String,
  callback: String
)

@unused
object Transport {
  implicit val schema: Schema[Transport] = DeriveSchema.gen[Transport]
  implicit val jsonDecoder: JsonDecoder[Transport] = DeriveJsonDecoder.gen[Transport]
}
