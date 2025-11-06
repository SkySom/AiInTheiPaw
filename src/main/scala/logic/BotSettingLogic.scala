package io.sommers.aiintheipaw
package logic

import model.channel.Channel
import model.guild.Guild
import model.problem.{Problem, ThrowableProblem}
import model.setting.BotSetting
import service.BotSettingService
import util.CacheHelper
import util.Enrichment.EnrichOption

import zio.cache.{Cache, Lookup}
import zio.{IO, URLayer, ZIO, ZLayer}

trait BotSettingLogic {
  def getValue[T](channel: Channel, setting: BotSetting[T]): IO[Problem, Option[T]]

  def getValue[T](channel: Channel, setting: BotSetting[T], defaultValue: T): IO[Problem, T] =
    getValue(channel, setting)
      .map(_.getOrElse(defaultValue))

  def setValue[T](channel: Channel, setting: BotSetting[T], value: T): IO[Problem, Boolean]

  def setValue[T](guild: Guild, setting: BotSetting[T], value: T): IO[Problem, Boolean]
}

object BotSettingLogic {
  val live: URLayer[BotSettingService, BotSettingLogic] = ZLayer.fromFunction(BotSettingLogicLive.apply)

  val cachedLive: URLayer[BotSettingService & ChannelLogic, BotSettingLogic] = live >>> ZLayer.fromZIO(
    {
      for {
        channelSettingLogic <- ZIO.service[BotSettingLogic]
        channelLogic <- ZIO.service[ChannelLogic]
        cache <- Cache.makeWith[(Channel, BotSetting[?]), Any, Problem, Option[Any]](
          capacity = Int.MaxValue,
          lookup = Lookup(key => channelSettingLogic.getValue(key._1, key._2))
        ) {
          CacheHelper.handleTTL
        }
      } yield BotSettingLogicCachedLive(cache, channelSettingLogic, channelLogic)
    }
  )
}

case class BotSettingLogicLive(
  botSettingService: BotSettingService
) extends BotSettingLogic {
  override def getValue[T](channel: Channel, setting: BotSetting[T]): IO[Problem, Option[T]] = for {
    dbValue <- botSettingService.getSetting(channel.guild.id, channel.id, setting.key)
      .mapError(ThrowableProblem(_))
    parsedValue <- dbValue.mapZIO(value => setting.readFrom(value.value))
  } yield parsedValue

  override def setValue[T](channel: Channel, setting: BotSetting[T], value: T): IO[Problem, Boolean] = for {
    insert <- botSettingService.setSetting(channel.guild.id, Some(channel.id), setting.key, setting.writeTo(value))
      .mapError(ThrowableProblem(_))
  } yield insert

  override def setValue[T](guild: Guild, setting: BotSetting[T], value: T): IO[Problem, Boolean] = for {
    insert <- botSettingService.setSetting(guild.id, None, setting.key, setting.writeTo(value))
      .mapError(ThrowableProblem(_))
  } yield insert
}

private case class BotSettingLogicCachedLive(
  cache: Cache[(Channel, BotSetting[?]), Problem, Option[Any]],
  logic: BotSettingLogic,
  channelLogic: ChannelLogic
) extends BotSettingLogic {
  override def getValue[T](channel: Channel, setting: BotSetting[T]): IO[Problem, Option[T]] = for {
    cachedValue <- cache.get(channel, setting)
    castValue <- cachedValue.mapZIO(value => ZIO.attempt(value.asInstanceOf[T]))
      .mapError(ThrowableProblem(_))
  } yield castValue

  override def setValue[T](channel: Channel, setting: BotSetting[T], value: T): IO[Problem, Boolean] = for {
    updated <- logic.setValue(channel, setting, value)
    _ <- cache.invalidate(channel, setting)
  } yield updated

  override def setValue[T](guild: Guild, setting: BotSetting[T], value: T): IO[Problem, Boolean] = for {
    channels <- channelLogic.getChannels(guild)
    updated <- logic.setValue(guild, setting, value)
    _ <- ZIO.foreach(channels)(cache.invalidate(_, setting))
  } yield updated
}
