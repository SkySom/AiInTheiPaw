package io.sommers.aiintheipaw
package model.channel

import model.guild.Guild
import model.service.Service

trait Channel {
  val id: Long

  val service: Service

  val guild: Guild

  val channelId: String

  val displayName: String
}

case class ChannelImpl(
  override val id: Long,
  override val channelId: String,
  override val service: Service,
  override val guild: Guild,
  override val displayName: String
) extends Channel

