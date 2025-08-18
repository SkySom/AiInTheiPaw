package io.sommers.aiintheipaw
package service

import model.service.Service
import model.user.{User, UserSource}

import io.sommers.zio.slick.DatabaseZIO
import database.AiPostgresProfile.api.*
import slick.lifted.{ForeignKeyQuery, Tag}
import zio.{Task, URLayer, ZLayer}

import scala.annotation.unused

case class UserEntity(
  id: Long
) {
  def toUser(userSources: Seq[UserSourceEntity]): User = User(id, userSources.map(_.toUserSource).toList)
}

class UserTable(tag: Tag) extends Table[UserEntity](tag, "\"user\"") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def * = id.mapTo[UserEntity]
}

val userQuery = TableQuery[UserTable]

case class UserSourceEntity(
  id: Long,
  userId: Long,
  service: Service,
  serviceUserId: String,
  displayName: String
) {
  def toUserSource: UserSource = UserSource(
    id,
    userId,
    service,
    serviceUserId,
    displayName
  )
}

class UserSourceTable(tag: Tag) extends Table[UserSourceEntity](tag, "user_source") {
  def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def userId = column[Long]("user_id")

  def service = column[Service]("service")

  def serviceUserId = column[String]("service_user_id")

  def displayName = column[String]("display_name")

  @unused
  def userIdForeignKey: ForeignKeyQuery[UserTable, UserEntity] = foreignKey("user_id_fk", userId, userQuery)(_.id)

  def * = (id, userId, service, serviceUserId, displayName).mapTo[UserSourceEntity]
}

val userSourceQuery = TableQuery[UserSourceTable]

trait UserService {
  def getUser(id: Long): Task[Option[(UserEntity, Seq[UserSourceEntity])]]

  def getUser(service: Service, userId: String): Task[Option[(UserEntity, Seq[UserSourceEntity])]]

  def createUser(service: Service, userId: String, displayName: String): Task[(UserEntity, UserSourceEntity)]

  def updateUserSource(userSourceEntity: UserSourceEntity): Task[Int]
}

private case class UserServiceLive(
  databaseZIO: DatabaseZIO
) extends UserService {

  override def getUser(id: Long): Task[Option[(UserEntity, Seq[UserSourceEntity])]] = {
    databaseZIO.runStream {
      val query = for {
        (user, userSource) <- userQuery.filter(_.id === id) join userSourceQuery on (_.id === _.userId)
      } yield (user, userSource)

      query.result
    }.map {
      result =>
        result.headOption.map(firstResult => (firstResult._1, result.map(_._2)))
    }
  }

  override def getUser(service: Service, userId: String): Task[Option[(UserEntity, Seq[UserSourceEntity])]] = {
    databaseZIO.runStream {
      {
        for {
          user <- userQuery.filter(_.id in userSourceQuery.filter(_.service === service)
            .filter(_.serviceUserId === userId)
            .map(_.userId)
          )
          userSources <- userSourceQuery if userSources.userId === user.id
        } yield (user, userSources)
      }.result
    }.map(results => results.headOption.map(result => (result._1, results.map(_._2))))
  }

  override def createUser(service: Service, userId: String, displayName: String): Task[(UserEntity, UserSourceEntity)] = {
    databaseZIO.run {
      implicit _ =>
        for {
          user <- (userQuery returning userQuery.map(_.id) into ((user, id) => user.copy(id = id))) += UserEntity(0L)
          userSource <- (userSourceQuery returning userSourceQuery.map(_.id) into ((user, id) => user.copy(id = id))) +=
            UserSourceEntity(0L, user.id, service, userId, displayName)
        } yield (user, userSource)
    }
  }

  override def updateUserSource(userSourceEntity: UserSourceEntity): Task[Int] = {
    databaseZIO.run {
      userSourceQuery.filter(_.id === userSourceEntity.id)
        .update(userSourceEntity)
    }
  }
}

object UserServiceLive {
  val live: URLayer[DatabaseZIO, UserService] = ZLayer.fromFunction(UserServiceLive(_))
}