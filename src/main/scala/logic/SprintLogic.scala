package io.sommers.aiintheipaw
package logic

import model.channel.Channel
import model.problem.{InvalidValueProblem, Problem}
import model.sprint.SprintStatus.SignUp
import model.sprint.{Sprint, SprintSection}
import model.user.User
import service.{CreateSprintEntity, CreateSprintSectionEntity, SprintService}

import org.postgresql.util.PGInterval
import zio.{Clock, Duration, IO, ZIO}

import java.time.temporal.ChronoUnit
import scala.language.implicitConversions


trait SprintLogic {
  def createSprint(channel: Channel, user: User, duration: Duration): IO[Problem, Sprint]
}

case class SprintLogicLive(
  sprintService: SprintService,
  clock: Clock
) extends SprintLogic {
  override def createSprint(channel: Channel, user: User, sprintDuration: Duration): IO[Problem, Sprint] = {
    for {
      activeSprint <- sprintService.getActiveSprintByChannelId(channel.id)
      _ <- ZIO.when(activeSprint.isDefined)(ZIO.fail(InvalidValueProblem("There is already an active sprint")))
      newSprint <- sprintService.createSprint(CreateSprintEntity(channel.id, user.id))
      startTime <- clock.instant
      signUpSection <- sprintService.updateSprintStatus(CreateSprintSectionEntity(newSprint.id, SignUp, sprintDuration, startTime))
    } yield Sprint(
      newSprint.id,
      channel,
      user,
      Seq(
        SprintSection(
          signUpSection.id,
          signUpSection.status,
          signUpSection.startTime.toInstant,
          signUpSection.totalTime
        )
      ),
      Seq()
    )
  }.mapError(Problem(_))

  implicit def pgIntervalToDuration(interval: PGInterval): Duration = {
    java.time.Duration.ofDays(interval.getDays)
      .plus(interval.getHours, ChronoUnit.HOURS)
      .plus(interval.getMinutes, ChronoUnit.MINUTES)
      .plus(interval.getWholeSeconds, ChronoUnit.SECONDS)
      .plus(interval.getMicroSeconds, ChronoUnit.MICROS)
  }
}