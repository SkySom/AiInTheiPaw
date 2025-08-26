package io.sommers.aiintheipaw
package mock.service

import model.service.Service
import service.{UserEntity, UserService, UserSourceEntity}

import zio.{Task, ULayer, ZIO, ZLayer}

import scala.collection.mutable

class UserServiceMock extends UserService {
  val users: mutable.Map[Long, (UserEntity, mutable.ArrayBuffer[UserSourceEntity])] = new mutable.HashMap()

  override def getUser(id: Long): Task[Option[(UserEntity, Seq[UserSourceEntity])]] = ZIO.succeed(users.get(id)
    .map(user => (user._1, user._2.toSeq))
  )

  override def getUser(service: Service, serviceUserId: String): Task[Option[(UserEntity, Seq[UserSourceEntity])]] =
    ZIO.succeed(users.find(user => user._2._2.exists(userSource => userSource.service == service && userSource.serviceUserId == serviceUserId))
      .map(user => user._2)
      .map(user => (user._1, user._2.toSeq))
    )

  override def createUser(service: Service, serviceUserId: String, displayName: String): Task[(UserEntity, UserSourceEntity)] = {
    val userId = users.keys.maxOption.getOrElse(1L)
    val userSourceId = users.values.map(_._2.map(_.id).max).maxOption.getOrElse(1L)

    val user = UserEntity(userId)
    val userSource = UserSourceEntity(userSourceId, userId, service, serviceUserId, displayName)

    users.put(userId, (user, mutable.ArrayBuffer(userSource)))
    ZIO.succeed((user, userSource))
  }

  override def updateUserSource(userSourceEntity: UserSourceEntity): Task[Int] = ???
}

object UserServiceMock {
  val mock: ULayer[UserServiceMock] = ZLayer.succeed(new UserServiceMock)
}
