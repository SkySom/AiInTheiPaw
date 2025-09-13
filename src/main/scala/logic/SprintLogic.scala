package io.sommers.aiintheipaw
package logic

import event.EventScheduler
import eventhandler.SprintSectionProgress
import model.channel.Channel
import model.problem.{InvalidValueProblem, NotFoundProblem, Problem}
import model.sprint.SprintStatus.SignUp
import model.sprint.{Sprint, SprintEntry, SprintSection, SprintStatus}
import model.user.User
import service.{SprintEntity, SprintEntryEntity, SprintSectionEntity, SprintService}
import util.Enrichment.{EnrichBoolean, EnrichOption, EnrichZIOOption}

import zio.{Duration, IO, URLayer, ZIO, ZLayer, durationInt}

import scala.language.implicitConversions


trait SprintLogic {
  def createSprint(channel: Channel, user: User, duration: Duration): ZIO[EventScheduler, Problem, Sprint]

  def getActiveSprintByChannel(channel: Channel): IO[Problem, Option[Sprint]]

  def joinSprint(channel: Channel, user: User, startingWords: Long): IO[Problem, SprintEntry]

  def progressSprint(currentSectionId: Long, nextSectionStatus: SprintStatus): ZIO[EventScheduler, Problem, SprintSection]

  def getSprintById(sprintId: Long): IO[Problem, Sprint]
}

object SprintLogic {
  def live: URLayer[SprintService & UserLogic & ChannelLogic, SprintLogic] = ZLayer.fromFunction(SprintLogicLive.apply)
}

case class SprintLogicLive(
  sprintService: SprintService,
  userLogic: UserLogic,
  channelLogic: ChannelLogic
) extends SprintLogic {
  override def createSprint(channel: Channel, user: User, sprintDuration: Duration): ZIO[EventScheduler, Problem, Sprint] = {
    for {
      activeSprint <- getActiveSprintByChannel(channel)
      _ <- ZIO.when(activeSprint.isDefined)(ZIO.fail(InvalidValueProblem("There is already an active sprint")))
      newSprint <- sprintService.createSprint(channel.id, user.id, sprintDuration)
      startTime <- ZIO.clockWith(_.instant)
      signUpSection <- sprintService.createSprintSection(newSprint.id, SignUp, 1.minute, startTime)
      _ <- ZIO.serviceWithZIO[EventScheduler](_.schedule(1.minute, "sprint", SprintSectionProgress(newSprint.id, signUpSection.id, SprintStatus.InProgress)))
    } yield Sprint(
      newSprint.id,
      channel,
      user,
      sprintDuration,
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

  override def joinSprint(channel: Channel, user: User, startingWords: Long): IO[Problem, SprintEntry] = for {
    activeSprint <- getActiveSprintByChannel(channel)
      .getOrFail(NotFoundProblem("sprint", "No active sprint for channel"))
    activeSection: SprintSection <- activeSprint.sections.lastOption
      .getOrZIOFail(InvalidValueProblem(s"No active section for sprint ${activeSprint.id}"))
    _ <- activeSection.status.allowSignUp.toZIO(InvalidValueProblem(s"Cannot sign up when sprint is in status ${activeSection.status}"))
    signUp <- sprintService.joinSprint(user.id, activeSection.id, startingWords)
      .mapError(Problem(_))
      .flatMap(entityToEntry)
  } yield signUp

  override def progressSprint(currentSectionId: Long, nextSectionStatus: SprintStatus): ZIO[EventScheduler, Problem, SprintSection] = for {
    sprintEntry <- sprintService.getSprintBySectionId(currentSectionId)
      .getOrFail(NotFoundProblem("sprint", s"No section with id $currentSectionId found"))
      .mapError(Problem(_))
    sprint <- entityToSprint(sprintEntry)
    startTime <- ZIO.clockWith(_.instant)
    sectionEntity <- sprintService.createSprintSection(sprint.id, nextSectionStatus, 1.minute, startTime)
      .mapError(Problem(_))
  } yield entityToSection(sectionEntity)

  override def getSprintById(sprintId: Long): IO[Problem, Sprint] = for {
    sprintEntry <- sprintService.getSprintById(sprintId)
      .getOrFail(NotFoundProblem("sprint", s"No sprint with id $sprintId found"))
      .mapError(Problem(_))
    sprint <- entityToSprint(sprintEntry)
  } yield sprint


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
    sprintEntryEntity.endingWords
  )

  private def entityToSprint(entities: (SprintEntity, Seq[SprintSectionEntity], Seq[SprintEntryEntity])): IO[Problem, Sprint] =
    entityToSprint(entities._1, entities._2, entities._3)

  private def entityToSprint(sprintEntity: SprintEntity, sprintSectionEntities: Seq[SprintSectionEntity], sprintEntryEntities: Seq[SprintEntryEntity]): IO[Problem, Sprint] = for {
    startedByUser <- userLogic.getUserById(sprintEntity.startedByUserId)
    channel <- channelLogic.getChannel(sprintEntity.channelId)
    entries <- ZIO.foldLeft(sprintEntryEntities)(List.empty[SprintEntry]) {
      (sprintEntries, entity) =>
        for {
          sprintEntry <- entityToEntry(entity)
        } yield sprintEntries.appended(sprintEntry)
    }
  } yield Sprint(
    sprintEntity.id,
    channel,
    startedByUser,
    sprintEntity.progressDuration,
    sprintSectionEntities.map(entityToSection),
    entries
  )
}