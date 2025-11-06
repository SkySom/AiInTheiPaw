package io.sommers.aiintheipaw
package eventhandler

import event.{EventHandler, EventScheduler}
import logic.message.MessageLogic
import logic.{SprintCommandLogic, SprintLogic}
import model.problem.{JsonParseProblem, Problem}
import model.sprint.SprintStatus

import io.sommers.zio.localize.Localizer
import zio.json.ast.Json
import zio.json.{JsonDecoder, JsonEncoder}
import zio.{URLayer, ZIO, ZLayer}

import java.util.Locale

case class SprintEventHandler(
  sprintCommandLogic: SprintCommandLogic,
) extends EventHandler {

  override def name: String = "sprint"

  override def handleEvent(json: Json): ZIO[EventScheduler, Problem, Unit] = for {
    event <- ZIO.fromEither(json.as[SprintSectionProgress])
      .mapError(JsonParseProblem(_))
    _ <- sprintCommandLogic.progressSprint(event.sprintId, event.currentSectionId, event.nextSectionStatus)
  } yield ()
}

object SprintEventHandler {
  val live: URLayer[SprintCommandLogic, SprintEventHandler] = ZLayer.fromFunction(SprintEventHandler.apply)
}

case class SprintSectionProgress(
  sprintId: Long,
  currentSectionId: Long,
  nextSectionStatus: SprintStatus
) derives JsonEncoder, JsonDecoder
