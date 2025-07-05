package io.sommers.aiintheipaw
package generator

import model.channel.{Channel, MockChannel}

import zio.{UIO, ZIO}

object TestChannelGenerator {
  def generateChannel(includeGuildId: Boolean = false): UIO[Channel] = for {
    id <- ZIO.randomWith(_.nextLong)
    channelId <- ZIO.randomWith(_.nextString(8))
    guildId <- ZIO.when(includeGuildId)(ZIO.randomWith(_.nextString(8)))
  } yield MockChannel(id, channelId, guildId)
}
