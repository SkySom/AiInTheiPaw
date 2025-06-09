package io.sommers.aiintheipaw
package logic

import model.channel.Channel
import model.problem.{NotFoundProblem, Problem, ThrowableProblem}
import model.service.Service
import service.{ChannelEntity, ChannelService}

import zio.cache.{Cache, Lookup}
import zio.{Duration, IO, URLayer, ZIO, ZLayer}

import java.sql.SQLException
import java.time.temporal.ChronoUnit

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
        ThrowableProblem.applyZIO(_),
        _.fold(createChannel(service, channelId, guildId)) {
          _.toChannel
        }
      )
  }

  private def createChannel(service: Service, channelId: String, guildId: Option[String]): IO[Problem, Channel] = {
    channelService.createChannel(ChannelEntity(0, channelId, service.name, guildId))
      .foldZIO(
        ThrowableProblem.applyZIO(_),
        _.toChannel
      )
  }

  override def getChannel(id: Long): IO[Problem, Channel] = channelService.getChannel(id)
    .foldZIO(
      sqlException => ZIO.fail(ThrowableProblem(sqlException)),
      channelEntityOpt => channelEntityOpt.fold[IO[Problem, Channel]](
        ZIO.fail(NotFoundProblem("Channel", s"No Channel with id $id"))
      ) {
        channelEntity => channelEntity.toChannel
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
          result => result.foldExit(
            failed = cause => cause.failureOption match {
              case Some(ThrowableProblem(_: SQLException)) => Duration.Zero
              case _ => Duration(1, ChronoUnit.MINUTES)
            },
            completed = _ => Duration(10, ChronoUnit.MINUTES)
          )
        }
        cacheByChannelInfo <- Cache.makeWith[(Service, String, Option[String]), Any, Problem, Channel](
          capacity = Int.MaxValue,
          lookup = Lookup(key => channelLogic.findChannelForService(key._1, key._2, key._3))
        ) {
          result => result.foldExit(
            failed = cause => cause.failureOption match {
              case Some(ThrowableProblem(_: SQLException)) => Duration.Zero
              case _ => Duration(1, ChronoUnit.MINUTES)
            },
            completed = _ => Duration(10, ChronoUnit.MINUTES)
          )
        }
      } yield ChannelLogicCachedLive(cacheById, cacheByChannelInfo)
    }
  )
}
