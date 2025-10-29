package io.sommers.aiintheipaw
package logic

import event.EventRouter
import eventhandler.SprintEventHandler
import generator.{TestChannelGenerator, TestGuildGenerator, TestUserGenerator}
import logic.SprintLogicSpec.test
import mock.MessageLogicMock
import mock.service.{ChannelServiceMock, BotSettingServiceMock, GuildServiceMock, SprintServiceMock, UserServiceMock}
import model.problem.{InvalidValueProblem, Problem}
import model.sprint.SprintStatus.{InProgress, SignUp}
import route.AiTestClient
import util.AiCustomAssertions

import io.sommers.zio.localize.{Localizer, ResourceProvider}
import zio.test.*
import zio.{Scope, ZIO, ZLayer, durationInt}

object SprintLogicSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment & Scope, Any] = suite("SprintLogicSpec")(
    test("createSprint() should create a Sprint if one doesn't exist") {
      for {
        user <- TestUserGenerator.generateAndInsertUser()
        channel <- TestChannelGenerator.generateAndInsertChannel()
        sprint <- ZIO.serviceWithZIO[SprintLogic](_.createSprint(channel, user, 1.minute, 1.minute))
      } yield assertTrue(sprint.sections.is(_.custom(AiCustomAssertions.seqHead)).status == SignUp)
    },
    test("createSprint() should fail to create a Sprint if one is active") {
      for {
        user <- TestUserGenerator.generateAndInsertUser()
        channel <- TestChannelGenerator.generateAndInsertChannel()
        sprint1 <- ZIO.serviceWithZIO[SprintLogic](_.createSprint(channel, user, 1.minute, 1.minute))
        sprint2 <- ZIO.serviceWithZIO[SprintLogic](_.createSprint(channel, user, 1.minute, 1.minute)).exit
      } yield assertTrue(sprint2.is(_.failure) == InvalidValueProblem("There is already an active sprint"))
    },
    test("joinSprint() should succeed if sprint in status that allows sign up") {
      for {
        user <- TestUserGenerator.generateAndInsertUser()
        channel <- TestChannelGenerator.generateAndInsertChannel()
        sprint <- ZIO.serviceWithZIO[SprintLogic](_.createSprint(channel, user, 1.minute, 1.minute))
        entry <- ZIO.serviceWithZIO[SprintLogic](_.joinSprint(channel, user, 125))
      } yield assertTrue(entry.startingWords == 125)
    },
    test("sprint should be in progress after 1 minute") {
      for {
        user <- TestUserGenerator.generateAndInsertUser()
        guild <- TestGuildGenerator.generateAndInsertGuild()
        channel <- TestChannelGenerator.generateAndInsertChannel()
        sprint <- ZIO.serviceWithZIO[SprintLogic](_.createSprint(channel, user, 1.minute, 1.minute))
        entry <- ZIO.serviceWithZIO[SprintLogic](_.joinSprint(channel, user, 125))
        _ <- TestClock.adjust(1.minute)
        inProgressSprint <- ZIO.serviceWithZIO[SprintLogic](_.getSprintById(sprint.id))
      } yield assertTrue(inProgressSprint.sections.is(_.custom(AiCustomAssertions.seqTail)).status == InProgress)
    }
  ).provide(
    SprintServiceMock.mock,
    UserServiceMock.mock >>> UserLogic.live,
    ChannelServiceMock.mock >>> ChannelLogic.live,
    SprintLogic.live,
    AiTestClient.layer,
    EventRouter.live,
    SprintEventHandler.live,
    ZLayer.collectAll(
      List(
        ZLayer.service[SprintEventHandler]
      )
    ),
    MessageLogicMock.mock,
    ResourceProvider.resourceBundleProvider("localization/localization") >>> Localizer.live,
    SprintCommandLogic.live,
    BotSettingLogic.live,
    ZLayer.succeed(SprintConfig(1.minute, 1.minute, 1.minute, 1.minute)),
    BotSettingServiceMock.mock,
    GuildLogic.live,
    GuildServiceMock.mock
  )
}
