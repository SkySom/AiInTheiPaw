package io.sommers.aiintheipaw
package mock.service

import model.service.Service
import service.{GuildEntity, GuildService}

import zio.{Task, ULayer, ZIO, ZLayer}

import scala.collection.mutable

case class GuildServiceMock(

) extends GuildService {
  val guilds: mutable.Map[Long, GuildEntity] = mutable.HashMap()

  override def getGuild(id: Long): Task[Option[GuildEntity]] = ZIO.succeed(guilds.get(id))

  override def getGuild(key: String, service: Service): Task[Option[GuildEntity]] = ZIO.succeed(guilds.values
    .find(guildEntity => guildEntity.service == service && guildEntity.key == key)
  )

  override def createGuild(key: String, service: Service, displayName: String): Task[GuildEntity] = {
    if (guilds.values.exists(guildEntity => guildEntity.service == service && guildEntity.key == key)) {
      ZIO.fail(new IllegalStateException("Already Exists"))
    } else {
      val id = guilds.keys.maxOption.getOrElse(1L)
      val guild = GuildEntity(id, key, service, displayName)
      guilds.put(id, guild)
      ZIO.succeed(guild)
    }
  }

  override def updateGuild(guildEntity: GuildEntity): Task[Int] = {
    if (guilds.keys.exists(_ == guildEntity.id)) {
      guilds.put(guildEntity.id, guildEntity)
      ZIO.succeed(1)
    } else {
      ZIO.succeed(0)
    }
  }
}

object GuildServiceMock {
  val mock: ULayer[GuildServiceMock] = ZLayer.succeed(new GuildServiceMock)
}
