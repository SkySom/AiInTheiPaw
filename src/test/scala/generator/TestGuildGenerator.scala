package io.sommers.aiintheipaw
package generator

import model.guild.Guild

import io.sommers.aiintheipaw.logic.GuildLogic
import io.sommers.aiintheipaw.model.problem.Problem
import io.sommers.aiintheipaw.model.service.Service
import io.sommers.aiintheipaw.model.service.Service.Test
import zio.{UIO, ZIO}

object TestGuildGenerator {
  def generateGuild(): UIO[Guild] = for {
    id <- ZIO.randomWith(_.nextLong)
    key <- ZIO.randomWith(_.nextString(8))
    displayName <- ZIO.randomWith(_.nextString(10))
  } yield Guild(id, key, Test, displayName)

  def generateAndInsertGuild(): ZIO[GuildLogic, Problem, Guild] = for {
    guildId <- ZIO.randomWith(_.nextString(8))
    displayName <- ZIO.randomWith(_.nextString(10))
    guild <- ZIO.serviceWithZIO[GuildLogic](_.findGuildForService(Service.Test, guildId, displayName))
  } yield guild
}
