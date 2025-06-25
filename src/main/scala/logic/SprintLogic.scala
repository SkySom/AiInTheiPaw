package io.sommers.aiintheipaw
package logic

import model.channel.Channel
import model.problem.{InvalidValueProblem, Problem}
import model.sprint.Sprint
import model.sprint.SprintStatus.SignUp
import model.user.{User, UserSource}
import service.{CreateSprintEntity, CreateSprintStatusEntity, SprintService}

import zio.{Clock, IO, ZIO}

import scala.concurrent.duration.Duration

trait SprintLogic {
  def createSprint(channel: Channel, user: User, duration: Duration): IO[Problem, Sprint]
}

case class SprintLogicLive(
  sprintService: SprintService,
  clock: Clock
) extends SprintLogic {
  override def createSprint(channel: Channel, user: User, duration: Duration): IO[Problem, Sprint] = {
    for {
      existingSprint <- sprintService.getSprintByChannelId(channel.id)
      _ <- ZIO.when(existingSprint.isDefined)(ZIO.fail(InvalidValueProblem("There is already an active sprint")))
      newSprint <- sprintService.createSprint(CreateSprintEntity(channel.id, user.id))
      startTime <- clock.instant
      signUpStatus <- sprintService.updateSprintStatus(CreateSprintStatusEntity(newSprint.id, SignUp, duration, startTime))
    } yield Sprint()
  }.mapError(Problem(_))
}