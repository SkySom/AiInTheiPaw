package io.sommers.aiintheipaw
package logic

import model.channel.{Channel, TwitchChannel}
import model.service.TwitchService

import zio.{IO, ULayer, ZIO, ZLayer}

class ChannelLogic {
  def getChannel(id: Long): IO[Throwable, Channel] = {
    ZIO.succeed(TwitchChannel(id, TwitchService(), id.toString))
  }
}

object ChannelLogic {
  val live: ULayer[ChannelLogic] = ZLayer.succeed(new ChannelLogic)
}
