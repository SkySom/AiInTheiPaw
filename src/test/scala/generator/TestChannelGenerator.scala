package io.sommers.aiintheipaw
package generator

import model.channel.{Channel, MockChannel}

import io.sommers.aiintheipaw.logic.ChannelLogic
import io.sommers.aiintheipaw.model.problem.Problem
import io.sommers.aiintheipaw.model.service.Service
import zio.{UIO, ZIO}

object TestChannelGenerator {
  def generateChannel(includeGuildId: Boolean = false): UIO[Channel] = for {
    id <- ZIO.randomWith(_.nextLong)
    channelId <- ZIO.randomWith(_.nextString(8))
    guildId <- ZIO.when(includeGuildId)(ZIO.randomWith(_.nextString(8)))
  } yield MockChannel(id, channelId, guildId)

  def generateAndInsertChannel(includeGuildId: Boolean = false): ZIO[ChannelLogic, Problem, Channel] = for {
    channelId <- ZIO.randomWith(_.nextString(8))
    guildId <- ZIO.when(includeGuildId)(ZIO.randomWith(_.nextString(8)))
    mockChannel <- ZIO.serviceWithZIO[ChannelLogic](_.findChannelForService(Service.Test, channelId, guildId))
  } yield mockChannel
}
