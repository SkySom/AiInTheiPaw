package io.sommers.aiintheipaw
package logic

import model.channel.{Channel, ChannelImpl}
import model.guild.Guild
import model.problem.{NotFoundProblem, Problem}
import model.service.Service
import service.{ChannelCreate, ChannelEntity, ChannelService}
import util.CacheHelper
import util.Enrichment.EnrichOption

import zio.cache.{Cache, Lookup}
import zio.{IO, URLayer, ZIO, ZLayer}

trait ChannelLogic {
  def getChannel(id: Long): IO[Problem, Channel]
  
  def getChannels(guild: Guild): IO[Problem, Seq[Channel]]

  def findChannelForService(service: Service, channelId: String, guild: Guild, displayName: String): IO[Problem, Channel]
}

case class ChannelLogicLive(
  channelService: ChannelService,
  guildLogic: GuildLogic
) extends ChannelLogic {

  override def findChannelForService(service: Service, channelId: String, guild: Guild, displayName: String): IO[Problem, Channel] = {
    for {
      existingChannel <- channelService.getChannel(service, channelId, guild.id)
      _ <- existingChannel.filter(_.displayName != displayName)
        .forEachZIO(channelService.updateChannel)
      channel <- existingChannel.map(createChannel(_, guild))
        .orElseZIO(createChannel(service, channelId, guild, displayName))
    } yield channel
  }.mapError(Problem(_))

  private def createChannel(service: Service, channelId: String, guild: Guild, displayName: String): IO[Problem, Channel] = {
    channelService.createChannel(ChannelCreate(channelId, service, guild.id, displayName))
      .map(createChannel(_, guild))
      .mapError(Problem(_))
  }

  override def getChannel(id: Long): IO[Problem, Channel] = channelService.getChannel(id)
    .foldZIO(
      Problem.applyZIO(_),
      {
        case Some(channelEntity) => guildLogic.getGuild(channelEntity.guildId)
          .map(guild => createChannel(channelEntity, guild))
        case _ => ZIO.fail(NotFoundProblem("channel", s"Failed to find Channel for $id"))
      }
    )

  override def getChannels(guild: Guild): IO[Problem, Seq[Channel]] = {
    channelService.getChannels(guild.id)
      .mapBoth(Problem(_), _.map(createChannel(_, guild)))
  }
  
  private def createChannel(channelEntity: ChannelEntity, guild: Guild): Channel = ChannelImpl(
    channelEntity.id,
    channelEntity.channelId,
    channelEntity.service,
    guild,
    channelEntity.displayName
  )
}

case class ChannelLogicCachedLive(
  cacheById: Cache[Long, Problem, Channel],
  cacheByChannel: Cache[(Service, String, Guild, String), Problem, Channel],
  channelLogic: ChannelLogic
) extends ChannelLogic {

  override def getChannel(id: Long): IO[Problem, Channel] = cacheById.get(id)

  override def findChannelForService(service: Service, channelId: String, guild: Guild, displayName: String): IO[Problem, Channel] =
    cacheByChannel.get(service, channelId, guild, displayName)

  //TODO: Figure out how best to handle this, since it's not easy to tell if this requires invalidation after 
  // findChannelForService calls
  override def getChannels(guild: Guild): IO[Problem, Seq[Channel]] = channelLogic.getChannels(guild)
}

object ChannelLogic {
  val live: URLayer[ChannelService & GuildLogic, ChannelLogic] = ZLayer.fromFunction(ChannelLogicLive.apply)

  val cachedLive: URLayer[ChannelService & GuildLogic, ChannelLogicCachedLive] = live >>> ZLayer.fromZIO(
    {
      for {
        channelLogic <- ZIO.service[ChannelLogic]
        cacheById <- Cache.makeWith(
          capacity = Int.MaxValue,
          lookup = Lookup(channelLogic.getChannel)
        ) {
          CacheHelper.handleTTL(_)
        }
        cacheByChannelInfo <- Cache.makeWith[(Service, String, Guild, String), Any, Problem, Channel](
          capacity = Int.MaxValue,
          lookup = Lookup(key => channelLogic.findChannelForService(key._1, key._2, key._3, key._4))
        ) {
          CacheHelper.handleTTL(_)
        }
      } yield ChannelLogicCachedLive(cacheById, cacheByChannelInfo, channelLogic)
    }
  )
}
