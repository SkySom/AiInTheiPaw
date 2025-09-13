package io.sommers.aiintheipaw
package eventhandler

import event.{EventHandler, EventScheduler}
import logic.SprintLogic
import logic.message.MessageLogic
import model.problem.{JsonParseProblem, Problem}
import model.sprint.SprintStatus

import io.sommers.zio.localize.Localizer
import zio.json.ast.Json
import zio.json.{JsonDecoder, JsonEncoder}
import zio.{ZIO, ZLayer}

import java.util.Locale

case class SprintEventHandler(
  sprintLogic: SprintLogic,
  messageLogic: MessageLogic,
  localizer: Localizer
) extends EventHandler {

  override def name: String = "sprint"

  override def handleEvent(json: Json): ZIO[EventScheduler, Problem, Unit] = for {
    event <- ZIO.fromEither(json.as[SprintSectionProgress])
      .mapError(JsonParseProblem(_))
    sprint <- sprintLogic.getSprintById(event.sprintId)
    nextSection <- sprintLogic.progressSprint(event.currentSectionId, event.nextSectionStatus)
    message <- localizer.localize(Locale.US, s"sprint.section.${nextSection.status}")
      .mapError(Problem(_))
    _ <- messageLogic.sendMessage(sprint.channel, message)
  } yield ()
}

object SprintEventHandler {
  def live: ZLayer[SprintLogic & MessageLogic & Localizer, Nothing, SprintEventHandler] = ZLayer.fromFunction(SprintEventHandler.apply)
}

case class SprintSectionProgress(
  sprintId: Long,
  currentSectionId: Long,
  nextSectionStatus: SprintStatus
) derives JsonEncoder, JsonDecoder
