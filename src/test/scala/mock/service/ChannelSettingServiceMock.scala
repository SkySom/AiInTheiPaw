package io.sommers.aiintheipaw
package mock.service

import service.{ChannelSettingEntity, ChannelSettingService}

import zio.{Task, ULayer, ZIO, ZLayer}

import scala.collection.mutable

case class ChannelSettingServiceMock() extends ChannelSettingService {
  val channelSettings: mutable.Map[Long, ChannelSettingEntity] = mutable.HashMap()

  override def getSetting(channelId: Long, key: String): Task[Option[ChannelSettingEntity]] = {
    ZIO.succeed(channelSettings.values
      .filter(channelSetting => channelSetting.channelId == channelId && channelSetting.key == key)
      .maxByOption(_.id)
    )
  }

  override def setSetting(channelId: Long, key: String, value: String): Task[Boolean] = {
    val id = channelSettings.keys.maxOption.getOrElse(1L)
    for {
      time <- ZIO.clockWith(_.instant)
      _ <- ZIO.succeed(channelSettings.put(
        id,
        ChannelSettingEntity(
          id,
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

object ChannelSettingServiceMock {
  val mock: ULayer[ChannelSettingServiceMock] = ZLayer.succeed(ChannelSettingServiceMock())
}
