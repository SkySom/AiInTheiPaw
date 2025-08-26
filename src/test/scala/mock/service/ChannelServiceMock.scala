package io.sommers.aiintheipaw
package mock.service

import model.service.Service
import service.{ChannelCreate, ChannelEntity, ChannelService}

import zio.{Task, ULayer, ZIO, ZLayer}

import scala.collection.mutable

class ChannelServiceMock extends ChannelService {
  val channels: mutable.Map[Long, ChannelEntity] = mutable.HashMap()

  override def getChannel(id: Long): Task[Option[ChannelEntity]] = ZIO.succeed(channels.get(id))

  override def getChannel(service: Service, channelId: String, guildId: Option[String]): Task[Option[ChannelEntity]] =
    ZIO.succeed(
      channels.find(
          channel => channel._2.service == service &&
            channel._2.channelId == channelId &&
            channel._2.guildId == guildId
        )
        .map(_._2)
    )

  override def createChannel(channelCreate: ChannelCreate): Task[ChannelEntity] = {
    val id = channels.keys.maxOption.getOrElse(1L)
    val channel = ChannelEntity(
      id,
      channelCreate.channelId,
      channelCreate.service,
      channelCreate.guildId
    )
    channels.put(id, channel)
    ZIO.succeed(channel)
  }
}

object ChannelServiceMock {
  def mock: ULayer[ChannelService] = ZLayer.succeed(new ChannelServiceMock)
}
