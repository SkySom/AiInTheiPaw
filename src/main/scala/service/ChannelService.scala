package io.sommers.aiintheipaw
package service

import database.CamelCaseNoEntitySqlNameMapper
import model.channel.Channel
import model.problem.NotFoundProblem
import model.service.Service

import com.augustnagro.magnum.*
import com.augustnagro.magnum.magzio.TransactorZIO
import zio.{IO, Task, URLayer, ZLayer}

import java.sql.SQLException
import javax.sql.DataSource
import scala.language.implicitConversions

@Table(PostgresDbType, CamelCaseNoEntitySqlNameMapper)
case class ChannelEntity(
  @Id id: Long,
  channelId: String,
  service: String,
  guildId: Option[String]
)derives DbCodec {
  def toChannel: IO[NotFoundProblem, Channel] = Channel(
    id,
    channelId,
    service,
    guildId
  )
}

trait ChannelService {
  def getChannel(id: Long): Task[Option[ChannelEntity]]

  def getChannel(service: Service, channelId: String, guildId: Option[String]): Task[Option[ChannelEntity]]

  def createChannel(channelEntity: ChannelEntity): Task[ChannelEntity]
}

case class ChannelServiceLive(
  transactorZIO: TransactorZIO
) extends ChannelService {

  private val channelRepo = Repo[ChannelEntity, ChannelEntity, Long]

  override def getChannel(id: Long): Task[Option[ChannelEntity]] = {
    transactorZIO.transact:
      channelRepo.findById(id)
  }

  override def createChannel(channelEntity: ChannelEntity): Task[ChannelEntity] = {
    transactorZIO.transact:
      channelRepo.insertReturning(channelEntity)
  }

  override def getChannel(service: Service, channelId: String, guildId: Option[String]): Task[Option[ChannelEntity]] = {
    val spec = Spec[ChannelEntity]
      .where(sql"service = ${service.toString}")
      .where(sql"channel_id = $channelId")
      .where(sql"guild_id = $guildId")

    transactorZIO.transact:
      channelRepo.findAll(spec)
        .headOption
  }
}

object ChannelServiceLive {
  val live: URLayer[TransactorZIO, ChannelServiceLive] = ZLayer.fromFunction(ChannelServiceLive(_))
}