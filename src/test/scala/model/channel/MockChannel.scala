package io.sommers.aiintheipaw
package model.channel

import model.service.Service

import io.sommers.aiintheipaw.model.service.Service.Test

case class MockChannel(
  override val id: Long,
  override val channelId: String,
  override val guildId: Option[String]
) extends Channel {
  override val service: Service = Test
}
