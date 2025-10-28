package io.sommers.aiintheipaw
package logic

import logic.GuildConversions.guildEntityToGuild
import model.guild.Guild
import model.problem.{NotFoundProblem, Problem}
import model.service.Service
import service.{GuildEntity, GuildService}
import util.Enrichment.EnrichOption

import zio.{IO, URLayer, ZLayer}

trait GuildLogic {
  def getGuild(id: Long): IO[Problem, Guild]

  def findGuildForService(service: Service, key: String, displayName: String): IO[Problem, Guild]
}

object GuildLogic {
  val live: URLayer[GuildService, GuildLogic] = ZLayer.fromFunction(GuildLogicLive.apply)
}

case class GuildLogicLive(
  guildService: GuildService
) extends GuildLogic {

  override def getGuild(id: Long): IO[Problem, Guild] = guildService.getGuild(id)
    .flatMap(_.getOrZIOFail(NotFoundProblem("guild", s"Couldn't find guild with id $id")))
    .map[Guild](_.convert)
    .mapError(Problem(_))

  override def findGuildForService(service: Service, key: String, displayName: String): IO[Problem, Guild] = {
    for {
      existingGuild <- guildService.getGuild(key, service)
      _ <- existingGuild.filter(_.displayName != displayName)
        .forEachZIO(guildService.updateGuild)
      guild <- existingGuild.map(_.convert)
        .orElseZIO(createGuild(service, key, displayName))
    } yield guild
  }.mapError(Problem(_))
  
  private def createGuild(service: Service, key: String, displayName: String): IO[Problem, Guild] =
    guildService.createGuild(key, service, displayName)
      .mapBoth(Problem(_), _.convert)
}

object GuildConversions:
  given guildEntityToGuild: Conversion[GuildEntity, Guild] with
    def apply(guildEntity: GuildEntity): Guild = Guild(
      guildEntity.id,
      guildEntity.key,
      guildEntity.service,
      guildEntity.displayName
    )

  given guildToGuildEntity: Conversion[Guild, GuildEntity] with
    def apply(guild: Guild): GuildEntity = GuildEntity(
      guild.id,
      guild.key,
      guild.service,
      guild.displayName
    )
