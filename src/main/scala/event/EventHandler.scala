package io.sommers.aiintheipaw
package event

import model.problem.Problem

import zio.ZIO
import zio.json.ast.Json

trait EventHandler {
  def name: String

  def handleEvent(json: Json): ZIO[EventScheduler, Problem, Unit]
}
