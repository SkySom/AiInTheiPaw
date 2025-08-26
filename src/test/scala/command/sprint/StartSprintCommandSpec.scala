package io.sommers.aiintheipaw
package command.sprint

import model.problem.Problem

import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}

object StartSprintCommandSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment & Scope, Problem] = suite("StartSprintCommandSpec")(
    test("run() should create a new Sprint if there is none") {
      assertTrue(true)
    }
  )
}
