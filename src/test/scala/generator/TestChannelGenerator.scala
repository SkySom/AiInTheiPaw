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
    displayName <- ZIO.randomWith(_.nextString(10))
  } yield MockChannel(id, channelId, guildId, displayName)

  def generateAndInsertChannel(includeGuildId: Boolean = false): ZIO[ChannelLogic, Problem, Channel] = for {
    channelId <- ZIO.randomWith(_.nextString(8))
    guildId <- ZIO.when(includeGuildId)(ZIO.randomWith(_.nextString(8)))
    displayName <- ZIO.randomWith(_.nextString(10))
    mockChannel <- ZIO.serviceWithZIO[ChannelLogic](_.findChannelForService(Service.Test, channelId, guildId, displayName))
  } yield mockChannel
}
