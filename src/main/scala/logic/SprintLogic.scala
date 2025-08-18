package io.sommers.aiintheipaw
package logic

import model.channel.Channel
import model.problem.{InvalidValueProblem, Problem}
import model.sprint.SprintStatus.SignUp
import model.sprint.{Sprint, SprintEntry, SprintSection}
import model.user.User
import service.{SprintEntity, SprintEntryEntity, SprintSectionEntity, SprintService}

import zio.{Duration, IO, URLayer, ZIO, ZLayer}

import scala.language.implicitConversions


trait SprintLogic {
  def createSprint(channel: Channel, user: User, duration: Duration): IO[Problem, Sprint]

  def getActiveSprintByChannel(channel: Channel): IO[Problem, Option[Sprint]]
}

object SprintLogic {
  val live: URLayer[SprintService & UserLogic & ChannelLogic, SprintLogic] = ZLayer.fromFunction(SprintLogicLive(_, _, _))
}

case class SprintLogicLive(
  sprintService: SprintService,
  userLogic: UserLogic,
  channelLogic: ChannelLogic
) extends SprintLogic {
  override def createSprint(channel: Channel, user: User, sprintDuration: Duration): IO[Problem, Sprint] = {
    for {
      activeSprint <- getActiveSprintByChannel(channel)
      _ <- ZIO.when(activeSprint.isDefined)(ZIO.fail(InvalidValueProblem("There is already an active sprint")))
      newSprint <- sprintService.createSprint(channel.id, user.id)
      startTime <- ZIO.clockWith(_.instant)
      signUpSection <- sprintService.createSprintSection(newSprint.id, SignUp, sprintDuration, startTime)
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

  override def getActiveSprintByChannel(channel: Channel): IO[Problem, Option[Sprint]] = for {
    optActiveSprint <- sprintService.getActiveSprintByChannelId(channel.id)
      .mapError(Problem(_))
    activeSprint: Option[Sprint] <- optActiveSprint.map[IO[Problem, Option[Sprint]]](activeSprint => entityToSprint(activeSprint._1, activeSprint._2, activeSprint._3).map(Some(_)))
      .getOrElse[IO[Problem, Option[Sprint]]](ZIO.succeed(None))
  } yield activeSprint


  private def entityToSection(sprintSectionEntity: SprintSectionEntity): SprintSection = SprintSection(
    sprintSectionEntity.id,
    sprintSectionEntity.status,
    sprintSectionEntity.startTime,
    sprintSectionEntity.totalTime
  )

  private def entityToEntry(sprintEntryEntity: SprintEntryEntity): IO[Problem, SprintEntry] = for {
    user <- userLogic.getUserById(sprintEntryEntity.userId)
  } yield SprintEntry(
    sprintEntryEntity.id,
    user,
    sprintEntryEntity.startSectionId,
    sprintEntryEntity.startingWords,
    sprintEntryEntity.endingWords,
    sprintEntryEntity.timeRemaining
  )

  private def entityToSprint(sprintEntity: SprintEntity, sprintSectionEntities: Seq[SprintSectionEntity], sprintEntryEntities: Seq[SprintEntryEntity]): IO[Problem, Sprint] = for {
    startedByUser <- userLogic.getUserById(sprintEntity.startedByUserId)
    channel <- channelLogic.getChannel(sprintEntity.channelId)
    entries <- ZIO.foldLeft(sprintEntryEntities)(List.empty[SprintEntry]) { (sprintEntries, entity) =>
      for {
        sprintEntry <- entityToEntry(entity)
      } yield sprintEntries.appended(sprintEntry)
    }
  } yield Sprint(
    sprintEntity.id,
    channel,
    startedByUser,
    sprintSectionEntities.map(entityToSection),
    entries
  )
}