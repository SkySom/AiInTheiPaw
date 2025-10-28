package io.sommers.aiintheipaw
package service

import database.AiPostgresProfile.api.*
import model.service.Service

import io.sommers.zio.slick.DatabaseZIO
import slick.lifted.{ForeignKeyQuery, Tag}
import zio.{Task, URLayer, ZLayer}

import scala.annotation.unused
import scala.language.implicitConversions

case class ChannelEntity(
  id: Long,
  channelId: String,
  service: Service,
  guildId: Long,
  displayName: String
)

case class ChannelCreate(
  channelId: String,
  service: Service,
  guildId: Long,
  displayName: String
)

class ChannelTable(tag: Tag) extends Table[ChannelEntity](tag, "channel") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def channelId = column[String]("channel_id")

  def service = column[Service]("service")

  def guildId = column[Long]("guild_id")

  def displayName = column[String]("display_name")

  @unused
  def guildIdForeignKey: ForeignKeyQuery[GuildTable, GuildEntity] = foreignKey("guild_id_fk", guildId, guildQuery)(_.id)

  def * = (id, channelId, service, guildId, displayName)
    .mapTo[ChannelEntity]
}

val channelQuery = TableQuery[ChannelTable]

trait ChannelService {
  def getChannel(id: Long): Task[Option[ChannelEntity]]

  def getChannel(service: Service, channelId: String, guildId: Long): Task[Option[ChannelEntity]]

  def createChannel(channelCreate: ChannelCreate): Task[ChannelEntity]

  def updateChannel(channelEntity: ChannelEntity): Task[Int]
}

case class ChannelServiceLive(
  databaseZIO: DatabaseZIO
) extends ChannelService {

  override def getChannel(id: Long): Task[Option[ChannelEntity]] = {
    databaseZIO.run(channelQuery.filter(_.id === id)
      .take(1)
      .result
      .headOption
    )
  }

  override def createChannel(channelCreate: ChannelCreate): Task[ChannelEntity] = {
    databaseZIO.run {
      (channelQuery returning channelQuery) += ChannelEntity(
        0,
        channelCreate.channelId,
        channelCreate.service,
        channelCreate.guildId,
        channelCreate.displayName
      )
    }
  }

  override def getChannel(service: Service, channelId: String, guildId: Long): Task[Option[ChannelEntity]] = {
    databaseZIO.run(channelQuery.filter(_.channelId === channelId)
      .filter(_.service === service)
      .filter(_.guildId === guildId)
      .take(1)
      .result
      .headOption
    )
  }

  override def updateChannel(channelEntity: ChannelEntity): Task[Int] = {
    databaseZIO.run(channelQuery.update(channelEntity))
  }
}

object ChannelServiceLive {
  val live: URLayer[DatabaseZIO, ChannelServiceLive] = ZLayer.fromFunction(ChannelServiceLive(_))
}