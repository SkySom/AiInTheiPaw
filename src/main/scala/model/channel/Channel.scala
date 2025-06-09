package io.sommers.aiintheipaw
package model.channel

import model.problem.NotFoundProblem
import model.service.{Service, TwitchService}

import zio.{IO, ZIO}

trait Channel {
  val id: Long

  val service: Service

  val guildId: Option[String]

  val channelId: String
}

case class TwitchChannel(
  id: Long,
  channelId: String
) extends Channel {
  override val guildId: Option[String] = None
  override val service: Service = TwitchService
}

object Channel {
  def apply(id: Long, channelId: String, service: String, guildId: Option[String]): IO[NotFoundProblem, Channel] = service match {
    case TwitchService.name => ZIO.succeed(TwitchChannel(id, channelId))
    case _ => ZIO.fail(NotFoundProblem("Channel", s"No Channel type for $service"))
  }
}
