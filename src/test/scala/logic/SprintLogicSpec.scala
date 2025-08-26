package io.sommers.aiintheipaw
package logic

import generator.{TestChannelGenerator, TestUserGenerator}
import mock.service.{ChannelServiceMock, SprintServiceMock, UserServiceMock}
import model.problem.{InvalidValueProblem, Problem}

import zio.test.*
import zio.{Scope, ZIO, durationInt}

object SprintLogicSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment & Scope, Any] = suite("SprintLogicSpec")(
    test("createSprint() should create a Sprint if one doesn't exist") {
      for {
        user <- TestUserGenerator.generateUser()
        channel <- TestChannelGenerator.generateChannel()
        sprint <- ZIO.serviceWithZIO[SprintLogic](_.createSprint(channel, user, 1.minute))
      } yield assertCompletes
    }.provide(layers),
    test("createSprint() should fail to create a Sprint if one is active") {
      for {
        user <- TestUserGenerator.generateAndInsertUser()
        channel <- TestChannelGenerator.generateAndInsertChannel()
        sprint1 <- ZIO.serviceWithZIO[SprintLogic](_.createSprint(channel, user, 1.minute))
        sprint2 <- ZIO.serviceWithZIO[SprintLogic](_.createSprint(channel, user, 1.minute)).exit
      } yield assertTrue(sprint2.is(_.failure) == InvalidValueProblem("There is already an active sprint"))
    }.provide(
      SprintServiceMock.mock,
      UserServiceMock.mock >>> UserLogic.live,
      ChannelServiceMock.mock >>> ChannelLogic.live,
      SprintLogic.live
    )
  )

  private def layers = SprintServiceMock.mock ++ (UserServiceMock.mock >>> UserLogic.live) ++
    (ChannelServiceMock.mock >>> ChannelLogic.live) >>> SprintLogic.live

}
