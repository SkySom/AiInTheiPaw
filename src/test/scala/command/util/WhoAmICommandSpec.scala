package io.sommers.aiintheipaw
package command.util

import generator.{TestChannelGenerator, TestMessageGenerator, TestUserGenerator}
import mock.MessageLogicMock
import model.problem.Problem

import zio.test.Assertion.*
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertCompletes, assert}
import zio.{Scope, ZIO}

object WhoAmICommandSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment & Scope, Problem] = suite("WhoAmICommandSpec")(
    test("run() completes successfully") {
      for {
        testUser <- TestUserGenerator.generateUser()
        testChannel <- TestChannelGenerator.generateChannel()
        testMessage <- TestMessageGenerator.generateMessage(testChannel, testUser)
        _ <- ZIO.serviceWithZIO[WhoAmICommand](_.run(testMessage, Map.empty))
        messageQueue <- ZIO.serviceWith[MessageLogicMock](_.getSentText(testChannel))
      } yield assert(messageQueue)(hasAt(0)(equalTo(s"You are [User ${testMessage.userSource.displayName}]")))
    }
  ).provide(WhoAmICommand.layer, MessageLogicMock.mock)
}
