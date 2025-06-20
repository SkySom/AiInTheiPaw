package io.sommers.aiintheipaw
package logic

import model.problem.{NotFoundProblem, Problem, ThrowableProblem}
import model.service.Service
import model.user.User
import service.{UserEntity, UserService, UserSourceEntity}
import util.CacheHelper

import zio.cache.{Cache, Lookup}
import zio.{IO, URLayer, ZIO, ZLayer}

trait UserLogic {
  def findUserForService(service: Service, userId: String, displayName: String): IO[Problem, User]

  def getUserById(id: Long): IO[Problem, User]
}

object UserLogic {
  val live: URLayer[UserService, UserLogic] = ZLayer.fromFunction(UserLogicLive(_))

  val cachedLive: URLayer[UserService, UserLogic] = live >>> ZLayer.fromZIO(
    {
      for {
        userLogic <- ZIO.service[UserLogic]
        cacheById <- Cache.makeWith(
          capacity = Int.MaxValue,
          lookup = Lookup(userLogic.getUserById)
        ) {
          CacheHelper.handleTTL(_)
        }
        cacheByService <- Cache.makeWith[(Service, String, String), Any, Problem, User](
          capacity = Int.MaxValue,
          lookup = Lookup(key => userLogic.findUserForService(key._1, key._2, key._3))
        ) {
          CacheHelper.handleTTL(_)
        }
      } yield UserLogicCachedLive(cacheById, cacheByService)
    }
  )
}

private case class UserLogicLive(
  userService: UserService
) extends UserLogic {

  override def findUserForService(service: Service, userId: String, displayName: String): IO[Problem, User] = {
    for {
      userSourceResult <- userService.getUserSource(service, userId)
      _ <- ZIO.when(userSourceResult.forall(!_.displayName.equals(displayName))) {
        userSourceResult.map(userSource => userService.updateUserSource(userSource.copy(displayName = displayName)))
          .getOrElse(ZIO.unit)
      }
      userResult <- userSourceResult.fold(createUser(service, userId, displayName)) {
        userSource => userService.getUser(userSource.userId)
      }
      (user, userSources) <- ZIO.fromOption(userResult)
        .mapError(_ => NotFoundProblem("user", s"Found UserSource ${userSourceResult.fold("Missing")(_)}, but no User"))
    } yield user.toUser(userSources)
  }.mapError(Problem(_))

  override def getUserById(id: Long): IO[Problem, User] = {
    for {
      result <- userService.getUser(id)
      (user, userSources) <- ZIO.fromOption(result)
        .mapError(_ => NotFoundProblem("user", s"No User with id $id"))
    } yield user.toUser(userSources)
  }.mapError(Problem(_))

  private def createUser(service: Service, userId: String, displayName: String): IO[Problem, Option[(UserEntity, Seq[UserSourceEntity])]] = {
    userService.createUser(service, userId, displayName)
      .map(tuple => Some(tuple._1, Seq(tuple._2)))
      .mapError(ThrowableProblem(_))
  }
}

private case class UserLogicCachedLive(
  cacheById: Cache[Long, Problem, User],
  cacheByService: Cache[(Service, String, String), Problem, User]
) extends UserLogic {
  override def findUserForService(service: Service, userId: String, displayName: String): IO[Problem, User] =
    cacheByService.get(service, userId, displayName)

  override def getUserById(id: Long): IO[Problem, User] = cacheById.get(id)
}

