package io.sommers.aiintheipaw
package generator

import model.channel.{Channel, MockChannel}

import io.sommers.aiintheipaw.logic.{ChannelLogic, GuildLogic}
import io.sommers.aiintheipaw.model.guild.Guild
import io.sommers.aiintheipaw.model.problem.Problem
import io.sommers.aiintheipaw.model.service.Service
import zio.{UIO, ZIO}

object TestChannelGenerator {
  def generateChannel(guild: Guild): UIO[Channel] = for {
    id <- ZIO.randomWith(_.nextLong)
    channelId <- ZIO.randomWith(_.nextString(8))
    displayName <- ZIO.randomWith(_.nextString(10))
  } yield MockChannel(id, channelId, guild, displayName)

  def generateChannel(): UIO[Channel] = for {
    guild <- TestGuildGenerator.generateGuild()
    id <- ZIO.randomWith(_.nextLong)
    channelId <- ZIO.randomWith(_.nextString(8))
    displayName <- ZIO.randomWith(_.nextString(10))
  } yield MockChannel(id, channelId, guild, displayName)

  def generateAndInsertChannel(guild: Guild): ZIO[ChannelLogic, Problem, Channel] = for {
    channelId <- ZIO.randomWith(_.nextString(8))
    displayName <- ZIO.randomWith(_.nextString(10))
    mockChannel <- ZIO.serviceWithZIO[ChannelLogic](_.findChannelForService(Service.Test, channelId, guild, displayName))
  } yield mockChannel

  def generateAndInsertChannel(): ZIO[ChannelLogic & GuildLogic, Problem, Channel] = for {
    guild <- TestGuildGenerator.generateAndInsertGuild()
    channelId <- ZIO.randomWith(_.nextString(8))
    displayName <- ZIO.randomWith(_.nextString(10))
    mockChannel <- ZIO.serviceWithZIO[ChannelLogic](_.findChannelForService(Service.Test, channelId, guild, displayName))
  } yield mockChannel
}
