package io.sommers.aiintheipaw
package logic

import model.channel.Channel
import model.channel.setting.ChannelSetting
import model.problem.{Problem, ThrowableProblem}
import service.ChannelSettingService
import util.CacheHelper
import util.Enrichment.EnrichOption

import zio.cache.{Cache, Lookup}
import zio.{IO, URLayer, ZIO, ZLayer}

trait ChannelSettingLogic {
  def getValue[T](channel: Channel, setting: ChannelSetting[T]): IO[Problem, Option[T]]

  def getValue[T](channel: Channel, setting: ChannelSetting[T], defaultValue: T): IO[Problem, T] =
    getValue(channel, setting)
      .map(_.getOrElse(defaultValue))

  def setValue[T](channel: Channel, setting: ChannelSetting[T], value: T): IO[Problem, Boolean]
}

object ChannelSettingLogic {
  val live: URLayer[ChannelSettingService, ChannelSettingLogic] = ZLayer.fromFunction(ChannelSettingLogicLive.apply)

  val cachedLive: URLayer[ChannelSettingService, ChannelSettingLogic] = live >>> ZLayer.fromZIO(
    {
      for {
        channelSettingLogic <- ZIO.service[ChannelSettingLogic]
        cache <- Cache.makeWith[(Channel, ChannelSetting[?]), Any, Problem, Option[Any]](
          capacity = Int.MaxValue,
          lookup = Lookup(key => channelSettingLogic.getValue(key._1, key._2))
        ) {
          CacheHelper.handleTTL
        }
      } yield ChannelSettingLogicCachedLive(cache, channelSettingLogic)
    }
  )
}

case class ChannelSettingLogicLive(
  channelSettingService: ChannelSettingService
) extends ChannelSettingLogic {
  override def getValue[T](channel: Channel, setting: ChannelSetting[T]): IO[Problem, Option[T]] = for {
    dbValue <- channelSettingService.getSetting(channel.id, setting.key)
      .mapError(ThrowableProblem(_))
    parsedValue <- dbValue.mapZIO(value => setting.readFrom(value.value))
  } yield parsedValue

  override def setValue[T](channel: Channel, setting: ChannelSetting[T], value: T): IO[Problem, Boolean] = for {
    insert <- channelSettingService.setSetting(channel.id, setting.key, setting.writeTo(value))
      .mapError(ThrowableProblem(_))
  } yield insert
}

private case class ChannelSettingLogicCachedLive(
  cache: Cache[(Channel, ChannelSetting[?]), Problem, Option[Any]],
  logic: ChannelSettingLogic
) extends ChannelSettingLogic {
  override def getValue[T](channel: Channel, setting: ChannelSetting[T]): IO[Problem, Option[T]] = for {
    cachedValue <- cache.get(channel, setting)
    castValue <- cachedValue.mapZIO(value => ZIO.attempt(value.asInstanceOf[T]))
      .mapError(ThrowableProblem(_))
  } yield castValue

  override def setValue[T](channel: Channel, setting: ChannelSetting[T], value: T): IO[Problem, Boolean] = for {
    _ <- cache.invalidate(channel, setting)
    updated <- logic.setValue(channel, setting, value)
  } yield updated
}
