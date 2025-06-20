package io.sommers.aiintheipaw
package service

import database.CamelCaseNoEntitySqlNameMapper
import model.channel.Channel
import model.problem.NotFoundProblem
import model.service.Service

import com.augustnagro.magnum.*
import com.augustnagro.magnum.magzio.TransactorZIO
import zio.{IO, Task, URLayer, ZLayer}

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

@Table(PostgresDbType, CamelCaseNoEntitySqlNameMapper)
case class ChannelCreate(
  channelId: String,
  service: String,
  guildId: Option[String]
) derives DbCodec

trait ChannelService {
  def getChannel(id: Long): Task[Option[ChannelEntity]]

  def getChannel(service: Service, channelId: String, guildId: Option[String]): Task[Option[ChannelEntity]]

  def createChannel(channelCreate: ChannelCreate): Task[ChannelEntity]
}

case class ChannelServiceLive(
  transactorZIO: TransactorZIO
) extends ChannelService {

  private val channelRepo = Repo[ChannelCreate, ChannelEntity, Long]

  override def getChannel(id: Long): Task[Option[ChannelEntity]] = {
    transactorZIO.connect:
      channelRepo.findById(id)
  }

  override def createChannel(channelCreate: ChannelCreate): Task[ChannelEntity] = {
    transactorZIO.connect:
      channelRepo.insertReturning(channelCreate)
  }

  override def getChannel(service: Service, channelId: String, guildIdOpt: Option[String]): Task[Option[ChannelEntity]] = {
    var spec = Spec[ChannelEntity]
      .where(sql"service = ${service.toString}")
      .where(sql"channel_id = $channelId")

    spec = guildIdOpt.fold(spec.where(sql"guild_id is null")) { guildId =>
      spec.where(sql"guild_id = $guildId")
    }
    
    transactorZIO.connect:
      channelRepo.findAll(spec)
        .headOption
  }
}

object ChannelServiceLive {
  val live: URLayer[TransactorZIO, ChannelServiceLive] = ZLayer.fromFunction(ChannelServiceLive(_))
}