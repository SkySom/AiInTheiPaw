package io.sommers.aiintheipaw
package service

import database.CamelCaseNoEntitySqlNameMapper
import model.service.Service
import model.user.{User, UserSource}

import com.augustnagro.magnum.*
import com.augustnagro.magnum.magzio.TransactorZIO
import zio.{Task, URLayer, ZLayer}

@Table(PostgresDbType, CamelCaseNoEntitySqlNameMapper)
case class UserEntity(
  @Id id: Long
)derives DbCodec {
  def toUser(userSources: Seq[UserSourceEntity]): User = User(id, userSources.map(_.toUserSource).toList)
}

@Table(PostgresDbType, CamelCaseNoEntitySqlNameMapper)
case class UserEntityCreator(

)derives DbCodec

@Table(PostgresDbType, CamelCaseNoEntitySqlNameMapper)
case class UserSourceEntity(
  @Id id: Long,
  userId: Long,
  service: String,
  serviceUserId: String,
  displayName: String
)derives DbCodec {
  def toUserSource: UserSource = UserSource(
    id,
    userId,
    Service.valueOf(service),
    serviceUserId,
    displayName
  )
}

@Table(PostgresDbType, CamelCaseNoEntitySqlNameMapper)
case class UserSourceEntityCreator(
  userId: Long,
  service: String,
  serviceUserId: String,
  displayName: String
)derives DbCodec

trait UserService {
  def getUser(id: Long): Task[Option[(UserEntity, Seq[UserSourceEntity])]]

  def getUserSource(service: Service, userId: String): Task[Option[UserSourceEntity]]

  def createUser(service: Service, userId: String, displayName: String): Task[(UserEntity, UserSourceEntity)]

  def updateUserSource(userSourceEntity: UserSourceEntity): Task[Unit]
}

private case class UserServiceLive(
  transactorZIO: TransactorZIO
) extends UserService {

  private val userRepo = Repo[UserEntityCreator, UserEntity, Long]
  private val userSourceRepo = Repo[UserSourceEntityCreator, UserSourceEntity, Long]


  private val createUserFrag: Returning[Long] = sql"""insert into "user" DEFAULT VALUES RETURNING id;""".returning[Long]

  override def getUser(id: Long): Task[Option[(UserEntity, Seq[UserSourceEntity])]] = {
    transactorZIO.transact {
      for {
        user <- userRepo.findById(id)
        userSources <- getUserSources(user.id)
      } yield (user, userSources)
    }
  }

  override def getUserSource(service: Service, userId: String): Task[Option[UserSourceEntity]] = {
    transactorZIO.connect {
      userSourceRepo.findAll(Spec[UserSourceEntity]
        .where(sql"service = ${service.name}")
        .where(sql"service_user_id = $userId")
      ).headOption
    }
  }

  private def getUserSources(userId: Long)(using dbCon: DbCon): Option[Seq[UserSourceEntity]] = {
    val sources = userSourceRepo.findAll(Spec[UserSourceEntity].where(sql"user_id = $userId"))
    if (sources.isEmpty) {
      None
    } else {
      Some(sources)
    }
  }

  override def createUser(service: Service, userId: String, displayName: String): Task[(UserEntity, UserSourceEntity)] = {
    transactorZIO.transact {
      val userEntity = UserEntity(id = createUserFrag.run().headOption.getOrElse(throw new IllegalStateException("No User Created")))
      val userSourceEntity = userSourceRepo.insertReturning(UserSourceEntityCreator(userEntity.id, service.name, userId, displayName))
      (userEntity, userSourceEntity)
    }
  }

  override def updateUserSource(userSourceEntity: UserSourceEntity): Task[Unit] = {
    transactorZIO.connect:
      userSourceRepo.update(userSourceEntity)
  }
}

object UserServiceLive {
  val live: URLayer[TransactorZIO, UserService] = ZLayer.fromFunction(UserServiceLive(_))
}