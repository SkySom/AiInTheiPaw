package io.sommers.aiintheipaw
package event

import model.problem.Problem

import zio.IO
import zio.json.ast.Json

trait EventHandler {
  def name: String

  def handleEvent(json: Json): IO[Problem, Unit]
}
