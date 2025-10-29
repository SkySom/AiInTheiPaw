package io.sommers.aiintheipaw
package service

import database.AiPostgresProfile.api.*

import io.sommers.zio.slick.DatabaseZIO
import zio.{Task, URLayer, ZIO, ZLayer}

import java.time.Instant
import scala.annotation.unused

case class BotSettingEntity(
  id: Long,
  guildId: Long,
  channelId: Option[Long],
  key: String,
  value: String,
  dateCreated: Instant
)

class ChannelSettingTable(tag: Tag) extends Table[BotSettingEntity](tag, "channel_setting") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def guildId = column[Long]("guild_id")
  
  def channelId = column[Option[Long]]("channel_id")

  def key = column[String]("key")

  def value = column[String]("value")

  def dateCreated = column[Instant]("date_created")

  def * = (id, guildId, channelId, key, value, dateCreated)
    .mapTo[BotSettingEntity]

  @unused
  def guildIdForeignKey = foreignKey("guild_id_fk", guildId, guildQuery)(_.id)
  
  @unused
  def channelIdForeignKey = foreignKey("channel_id_fk", channelId, channelQuery)(_.id.?)
  
  @unused
  def idx = index("idx", (guildId, channelId, key), unique = true)
}

val channelSettingQuery = TableQuery[ChannelSettingTable]


trait BotSettingService {
  def getSetting(guildId: Long, channelId: Long, key: String): Task[Option[BotSettingEntity]]

  def setSetting(guildId: Long, channelId: Option[Long], key: String, value: String): Task[Boolean]
}

object BotSettingService {
  val live: URLayer[DatabaseZIO, BotSettingService] = ZLayer.fromFunction(BotSettingServiceLive.apply)
}

case class BotSettingServiceLive(
  database: DatabaseZIO
) extends BotSettingService {

  override def getSetting(guildId: Long, channelId: Long, key: String): Task[Option[BotSettingEntity]] = 
    database.run(getSettingDBIO(channelId, key))
      .map(botSettings => {
        val botSettingsByChannel = botSettings.groupBy(_.channelId)
        
        val settings = if (botSettingsByChannel.contains(Some(channelId))) {
          botSettingsByChannel.get(Some(channelId))
        } else {
          botSettingsByChannel.get(None)
        }
        
        settings.flatMap(_.reduceOption((bs1, bs2) => if (bs1.dateCreated.isAfter(bs2.dateCreated)) bs1 else bs2))
      })

  override def setSetting(guildId: Long, channelId: Option[Long], key: String, value: String): Task[Boolean] = for {
    now <- ZIO.clockWith(_.instant)
    updatedCount <- database.run(channelSettingQuery += BotSettingEntity(0, guildId, channelId, key, value, now))
  } yield updatedCount == 1

  private def getSettingDBIO(guildId: Long, key: String): DBIO[Seq[BotSettingEntity]] = {
    channelSettingQuery.filter(_.guildId === guildId)
      .filter(_.key === key)
      .sortBy(_.dateCreated.desc)
      .result
  }
}
