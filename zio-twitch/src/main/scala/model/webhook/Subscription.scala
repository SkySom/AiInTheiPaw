package io.sommers.zio.twitch
package model.webhook

import zio.schema.{DeriveSchema, Schema}
import zio.schema.annotation.fieldName

import java.net.URL
import java.time.Instant


case class Subscription(
  id: String,
  status: String,
  @fieldName("type") subType: String,
  version: String,
  cost: Int,
  condition: Map[String, String],
  transport: Transport,
  @fieldName("created_at") createdAt: Instant
)

object Subscription {
  implicit val schema: Schema[Subscription] = DeriveSchema.gen[Subscription]
}

case class Transport(
  method: String,
  callback: URL
)

object Transport {
  implicit val schema: Schema[Transport] = DeriveSchema.gen[Transport]
}
