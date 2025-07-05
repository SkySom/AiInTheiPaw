package io.sommers.aiintheipaw
package generator

import model.service.Service.Test
import model.user.{User, UserSource}

import zio.{UIO, ZIO}

object TestUserGenerator {

  def generateUser(): UIO[User] = for {
    userSource <- generateUserSource()
  } yield User(userSource.userId, List(userSource))

  def generateUserSource(): UIO[UserSource] = for {
    id <- ZIO.randomWith(_.nextLong)
    userId <- ZIO.randomWith(_.nextLong)
    userServiceId <- ZIO.randomWith(_.nextString(10))
    displayName <- ZIO.randomWith(_.nextString(10))
  } yield UserSource(id, userId, Test, userServiceId, displayName)
}
