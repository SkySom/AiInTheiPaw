package io.sommers.aiintheipaw
package service

import model.channel.Channel
import model.problem.NotFoundProblem
import model.service.Service

import io.getquill._
import zio.{IO, URLayer, ZLayer}

import java.sql.SQLException
import javax.sql.DataSource
import scala.language.implicitConversions

case class ChannelEntity(
  id: Long,
  channelId: String,
  service: String,
  guildId: Option[String]
) {
  def toChannel: IO[NotFoundProblem, Channel] = Channel(
    id,
    channelId,
    service,
    guildId
  )
}

trait ChannelService {
  def getChannel(id: Long): IO[SQLException, Option[ChannelEntity]]

  def getChannel(service: Service, channelId: String, guildId: Option[String]): IO[SQLException, Option[ChannelEntity]]

  def createChannel(channelEntity: ChannelEntity): IO[SQLException, ChannelEntity]
}

case class ChannelServiceLive(
  dataSource: DataSource
) extends ChannelService {
  private implicit val context: PostgresZioJdbcContext[SnakeCase] = new PostgresZioJdbcContext[SnakeCase](SnakeCase)

  import context._

  private implicit val schema: Quoted[EntityQuery[ChannelEntity]] = quote {
    querySchema[ChannelEntity](
      "channel",
      _.id -> "id",
      _.channelId -> "channel_id",
      _.service -> "service",
      _.guildId -> "guild_id"
    )
  }

  override def getChannel(id: Long): IO[SQLException, Option[ChannelEntity]] = {
    for {
      result <- run {
        quote {
          schema.filter(_.id == lift(id))
        }
      }
    } yield result.headOption
  }.provide(ZLayer.succeed(dataSource))

  override def createChannel(channelEntity: ChannelEntity): IO[SQLException, ChannelEntity] = {
    for {
      result <- run {
        quote {
          schema.insertValue(lift(channelEntity))
            .returningGenerated(_.id)
        }
      }
    } yield channelEntity.copy(id = result)
  }.provide(ZLayer.succeed(dataSource))

  override def getChannel(service: Service, channelId: String, guildId: Option[String]): IO[SQLException, Option[ChannelEntity]] = {
    for {
      result <- run {
        quote {
          schema.filter(channel => channel.channelId == lift(channelId))
            .filter(channel => channel.service == lift(service.name))
            .filter(channel => channel.guildId == lift(guildId))
        }
      }
    } yield result.headOption
  }.provide(ZLayer.succeed(dataSource))
}

object ChannelServiceLive {
  val live: URLayer[DataSource, ChannelServiceLive] = ZLayer.fromFunction(ChannelServiceLive(_))
}