package io.sommers.aiintheipaw
package model.channel

import model.guild.Guild
import model.service.Service
import model.service.Service.Test

case class MockChannel(
  override val id: Long,
  override val channelId: String,
  override val guild: Guild,
  override val displayName: String
) extends Channel {
  override val service: Service = Test
}
