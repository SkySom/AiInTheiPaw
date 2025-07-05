package io.sommers.aiintheipaw
package logic

import model.channel.Channel
import model.problem.{InvalidValueProblem, Problem}
import model.sprint.SprintStatus.SignUp
import model.sprint.{Sprint, SprintSection}
import model.user.User
import service.{CreateSprintEntity, CreateSprintSectionEntity, SprintEntity, SprintEntryEntity, SprintSectionEntity, SprintService}

import org.postgresql.util.PGInterval
import zio.{Clock, Duration, IO, Task, URLayer, ZIO, ZLayer}

import java.time.temporal.ChronoUnit
import scala.language.implicitConversions


trait SprintLogic {
  def createSprint(channel: Channel, user: User, duration: Duration): IO[Problem, Sprint]
}

object SprintLogic {
  val live: URLayer[SprintService, SprintLogic] = ZLayer.fromFunction(SprintLogicLive(_))
}

case class SprintLogicLive(
  sprintService: SprintService,
) extends SprintLogic {
  override def createSprint(channel: Channel, user: User, sprintDuration: Duration): IO[Problem, Sprint] = {
    for {
      activeSprint <- getActiveSprintByChannelId(channel)
      _ <- ZIO.when(activeSprint.isDefined)(ZIO.fail(InvalidValueProblem("There is already an active sprint")))
      newSprint <- sprintService.createSprint(CreateSprintEntity(channel.id, user.id))
      startTime <- ZIO.clockWith(_.instant)
      signUpSection <- sprintService.updateSprintStatus(CreateSprintSectionEntity(newSprint.id, SignUp, sprintDuration, startTime))
    } yield Sprint(
      newSprint.id,
      channel,
      user,
      Seq(
        entityToSection(signUpSection)
      ),
      Seq()
    )
  }.mapError(Problem(_))
  
  protected def getActiveSprintByChannelId(channel: Channel): Task[Option[(SprintEntity, Seq[SprintSectionEntity], Seq[SprintEntryEntity])]] = 
    sprintService.getActiveSprintByChannelId(channel.id)

  implicit def pgIntervalToDuration(interval: PGInterval): Duration = {
    java.time.Duration.ofDays(interval.getDays)
      .plus(interval.getHours, ChronoUnit.HOURS)
      .plus(interval.getMinutes, ChronoUnit.MINUTES)
      .plus(interval.getWholeSeconds, ChronoUnit.SECONDS)
      .plus(interval.getMicroSeconds, ChronoUnit.MICROS)
  }
  
  private def entityToSection(sprintSectionEntity: SprintSectionEntity): SprintSection = SprintSection(
    sprintSectionEntity.id,
    sprintSectionEntity.status,
    sprintSectionEntity.startTime.toInstant,
    sprintSectionEntity.totalTime
  )
}