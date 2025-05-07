package io.sommers.aiintheipaw
package model.channel

import model.service.Service

trait Channel {
  val id: Long

  val service: Service

  val guildId: Option[String]

  val channelId: String
}

case class TwitchChannel(
  id: Long,
  service: Service,
  channelId: String
) extends Channel {
  override val guildId: Option[String] = None
}
