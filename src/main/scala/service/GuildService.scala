package io.sommers.aiintheipaw
package service

import database.AiPostgresProfile.api.*
import model.service.Service

import io.sommers.zio.slick.DatabaseZIO
import zio.{Task, URLayer, ZLayer}

case class GuildEntity(
  id: Long,
  key: String,
  service: Service,
  displayName: String
)

class GuildTable(tag: Tag) extends Table[GuildEntity](tag, "guild") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def key = column[String]("key")

  def service = column[Service]("service")

  def displayName = column[String]("display_name")

  def idx = index("idx", (key, service), unique = true)

  def * = (id, key, service, displayName).mapTo[GuildEntity]
}

val guildQuery = TableQuery[GuildTable]

trait GuildService {
  def getGuild(key: String, service: Service): Task[Option[GuildEntity]]

  def getGuild(id: Long): Task[Option[GuildEntity]]

  def createGuild(key: String, service: Service, displayName: String): Task[GuildEntity]

  def updateGuild(guildEntity: GuildEntity): Task[Int]
}

object GuildService {
  val live: URLayer[DatabaseZIO, GuildService] = ZLayer.fromFunction(GuildServiceLive.apply)
}

case class GuildServiceLive(
  databaseZIO: DatabaseZIO
) extends GuildService {

  override def getGuild(key: String, service: Service): Task[Option[GuildEntity]] = databaseZIO.run(
    guildQuery.filter(_.key === key)
      .filter(_.service === service)
      .take(1)
      .result
      .headOption
  )

  override def getGuild(id: Long): Task[Option[GuildEntity]] = databaseZIO.run(guildQuery.filter(_.id === id)
    .take(1)
    .result
    .headOption
  )

  override def createGuild(key: String, service: Service, displayName: String): Task[GuildEntity] = databaseZIO.run {
    val guildEntity = GuildEntity(0L, key, service, displayName)
    (guildQuery returning guildQuery.map(_.id)).into((guild, id) => guild.copy(id = id)) += guildEntity
  }

  override def updateGuild(guildEntity: GuildEntity): Task[Int] = databaseZIO.run(guildQuery.update(guildEntity))
}
