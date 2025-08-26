package io.sommers.aiintheipaw
package model.channel

import model.service.Service

trait Channel {
  val id: Long

  val service: Service

  val guildId: Option[String]

  val channelId: String
}

case class ChannelImpl(
  override val id: Long,
  override val channelId: String,
  override val service: Service,
  override val guildId: Option[String]
) extends Channel

object Channel {
  def apply(id: Long, channelId: String, service: Service, guildId: Option[String]): Channel = ChannelImpl(
    id,
    channelId,
    service,
    guildId
  )
}
