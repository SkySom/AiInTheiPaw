package io.sommers.aiintheipaw
package service

import database.AiPostgresProfile.api.*

import io.sommers.zio.slick.DatabaseZIO
import zio.{Task, URLayer, ZIO, ZLayer}

import java.time.Instant
import scala.annotation.unused

case class ChannelSettingEntity(
  id: Long,
  channelId: Long,
  key: String,
  value: String,
  dateCreated: Instant
)

class ChannelSettingTable(tag: Tag) extends Table[ChannelSettingEntity](tag, "channel_setting") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def channelId = column[Long]("channel_id")

  def key = column[String]("key")

  def value = column[String]("value")

  def dateCreated = column[Instant]("date_created")

  def * = (id, channelId, key, value, dateCreated)
    .mapTo[ChannelSettingEntity]

  @unused
  def channelIdForeignKey = foreignKey("channel_id_fk", channelId, channelQuery)(_.id)
}

val channelSettingQuery = TableQuery[ChannelSettingTable]


trait ChannelSettingService {
  def getSetting(channelId: Long, key: String): Task[Option[ChannelSettingEntity]]

  def setSetting(channelId: Long, key: String, value: String): Task[Boolean]
}

object ChannelSettingService {
  val live: URLayer[DatabaseZIO, ChannelSettingService] = ZLayer.fromFunction(ChannelSettingServiceLive.apply)
}

case class ChannelSettingServiceLive(
  database: DatabaseZIO
) extends ChannelSettingService {

  override def getSetting(channelId: Long, key: String): Task[Option[ChannelSettingEntity]] = database.run(
    getSettingDBIO(channelId, key)
  )

  override def setSetting(channelId: Long, key: String, value: String): Task[Boolean] = for {
    now <- ZIO.clockWith(_.instant)
    updatedCount <- database.run(channelSettingQuery += ChannelSettingEntity(0, channelId, key, value, now))
  } yield updatedCount == 1

  private def getSettingDBIO(channelId: Long, key: String): DBIO[Option[ChannelSettingEntity]] = {
    channelSettingQuery.filter(_.channelId === channelId)
      .filter(_.key === key)
      .sortBy(_.dateCreated.desc)
      .take(1)
      .result
      .headOption
  }
}
