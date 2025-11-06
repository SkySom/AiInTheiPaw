package io.sommers.aiintheipaw
package mock.service

import service.{BotSettingEntity, BotSettingService}

import zio.{Task, ULayer, ZIO, ZLayer}

import scala.collection.mutable

case class BotSettingServiceMock() extends BotSettingService {
  val botSettings: mutable.Map[Long, BotSettingEntity] = mutable.HashMap()

  override def getSetting(guildId: Long, channelId: Long, key: String): Task[Option[BotSettingEntity]] = {
    ZIO.succeed(botSettings.values
      .filter(botSetting => botSetting.guildId == guildId && botSetting.channelId.forall(_ == channelId) && botSetting.key == key)
      .groupBy(_.channelId)
      .map((_, settings) => settings.maxByOption(_.id))
      .flatMap(_.iterator)
      .reduceOption {
        case (bs1, bs2) if bs1.channelId.isDefined && bs2.channelId.isEmpty => bs1
        case (bs1, bs2) if bs1.channelId.isEmpty && bs2.channelId.isDefined => bs2
        case (bs1, bs2) => bs1
      }
    )
  }

  override def setSetting(guildId: Long, channelId: Option[Long], key: String, value: String): Task[Boolean] = {
    val id = botSettings.keys.maxOption.getOrElse(1L)
    for {
      time <- ZIO.clockWith(_.instant)
      _ <- ZIO.succeed(
        botSettings.put(
          id,
          BotSettingEntity(
            id,
            guildId,
            channelId,
            key,
            value,
            time
          )
        )
      )
    } yield true
  }
}

object BotSettingServiceMock {
  val mock: ULayer[BotSettingServiceMock] = ZLayer.succeed(BotSettingServiceMock())
}
