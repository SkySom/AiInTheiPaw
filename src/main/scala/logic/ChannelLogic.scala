package io.sommers.aiintheipaw
package logic

import model.channel.Channel
import model.problem.{NotFoundProblem, Problem}
import model.service.Service
import service.{ChannelCreate, ChannelService}
import util.CacheHelper

import zio.cache.{Cache, Lookup}
import zio.{IO, URLayer, ZIO, ZLayer}

trait ChannelLogic {
  def getChannel(id: Long): IO[Problem, Channel]

  def findChannelForService(service: Service, channelId: String, guildId: Option[String] = None): IO[Problem, Channel]
}

case class ChannelLogicLive(
  channelService: ChannelService,
) extends ChannelLogic {

  override def findChannelForService(service: Service, channelId: String, guildId: Option[String]): IO[Problem, Channel] = {
    channelService.getChannel(service, channelId, guildId)
      .foldZIO(
        Problem.applyZIO,
        _.fold(createChannel(service, channelId, guildId)) {
          channelEntity => ZIO.succeed(channelEntity.toChannel)
        }
      )
  }

  private def createChannel(service: Service, channelId: String, guildId: Option[String]): IO[Problem, Channel] = {
    channelService.createChannel(ChannelCreate(channelId, service, guildId))
      .map(_.toChannel)
      .mapError(Problem(_))
  }

  override def getChannel(id: Long): IO[Problem, Channel] = channelService.getChannel(id)
    .foldZIO(
      Problem.applyZIO(_),
      {
        case Some(channelEntity) => ZIO.succeed(channelEntity.toChannel)
        case _ => ZIO.fail(NotFoundProblem(s"Failed to find Channel for $id"))
      }
    )
}

case class ChannelLogicCachedLive(
  cacheById: Cache[Long, Problem, Channel],
  cacheByChannel: Cache[(Service, String, Option[String]), Problem, Channel]
) extends ChannelLogic {

  override def getChannel(id: Long): IO[Problem, Channel] = cacheById.get(id)

  override def findChannelForService(service: Service, channelId: String, guildId: Option[String]): IO[Problem, Channel] =
    cacheByChannel.get(service, channelId, guildId)
}

object ChannelLogic {
  val live: URLayer[ChannelService, ChannelLogic] = ZLayer.fromFunction(ChannelLogicLive(_))

  val cachedLive: ZLayer[ChannelService, Nothing, ChannelLogicCachedLive] = live >>> ZLayer.fromZIO(
    {
      for {
        channelLogic <- ZIO.service[ChannelLogic]
        cacheById <- Cache.makeWith(
          capacity = Int.MaxValue,
          lookup = Lookup(channelLogic.getChannel)
        ) {
          CacheHelper.handleTTL(_)
        }
        cacheByChannelInfo <- Cache.makeWith[(Service, String, Option[String]), Any, Problem, Channel](
          capacity = Int.MaxValue,
          lookup = Lookup(key => channelLogic.findChannelForService(key._1, key._2, key._3))
        ) {
          CacheHelper.handleTTL(_)
        }
      } yield ChannelLogicCachedLive(cacheById, cacheByChannelInfo)
    }
  )
}
