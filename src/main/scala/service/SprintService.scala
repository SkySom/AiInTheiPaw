package io.sommers.aiintheipaw
package service

import database.AiPostgresProfile.api.*
import model.sprint.SprintStatus

import io.sommers.zio.slick.DatabaseZIO
import slick.lifted.{ForeignKeyQuery, ProvenShape, TableQuery, Tag}
import zio.{Duration, Task, URLayer, ZLayer}

import java.time.Instant
import scala.annotation.unused
import scala.concurrent.ExecutionContext


case class SprintEntity(
  id: Long,
  channelId: Long,
  startedByUserId: Long,
)

class SprintTable(tag: Tag) extends Table[SprintEntity](tag, "sprint") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def channelId = column[Long]("channel_id")

  private def startedById = column[Long]("started_by_id")

  @unused
  def channelIdForeignKey: ForeignKeyQuery[ChannelTable, ChannelEntity] = foreignKey("channel_id_fk", channelId, channelQuery)(_.id)

  @unused
  def userIdForeignKey: ForeignKeyQuery[UserTable, UserEntity] = foreignKey("started_by_id_fk", startedById, userQuery)(_.id)

  def * : ProvenShape[SprintEntity] = (id, channelId, startedById).mapTo[SprintEntity]
}

val sprintQuery = TableQuery[SprintTable]

case class SprintSectionEntity(
  id: Long,
  sprintId: Long,
  status: SprintStatus,
  totalTime: Duration,
  startTime: Instant
)

class SprintSectionTable(tag: Tag) extends Table[SprintSectionEntity](tag, "sprint_section") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def sprintId = column[Long]("sprint_id")

  def status = column[SprintStatus]("status")

  def totalTime = column[Duration]("total_time")

  def startTime = column[Instant]("start_time")

  @unused
  def sprintIdForeignKey = foreignKey("sprint_id_fk", sprintId, sprintQuery)(_.id)

  def * : ProvenShape[SprintSectionEntity] = (id, sprintId, status, totalTime, startTime).mapTo[SprintSectionEntity]
}

val sprintSectionQuery = TableQuery[SprintSectionTable]

case class SprintEntryEntity(
  id: Long,
  userId: Long,
  sprintId: Long,
  startSectionId: Long,
  startingWords: Long,
  endingWords: Option[Long]
)

class SprintEntryTable(tag: Tag) extends Table[SprintEntryEntity](tag, "sprint_entry") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def userId = column[Long]("user_id")

  def sprintId = column[Long]("sprint_id")

  def startSectionId = column[Long]("start_section_id")

  def startingWords = column[Long]("starting_words")

  def endingWords = column[Option[Long]]("ending_words")

  @unused
  def userIdForeignKey: ForeignKeyQuery[UserTable, UserEntity] = foreignKey("user_id_fk", userId, userQuery)(_.id)

  @unused
  def sprintIdForeignKey = foreignKey("sprint_id_fk", sprintId, sprintQuery)(_.id)

  @unused
  def startSectionIdForeignKey = foreignKey("start_section_id_fk", startSectionId, sprintSectionQuery)(_.id)

  def * : ProvenShape[SprintEntryEntity] = (id, userId, sprintId, startSectionId, startingWords, endingWords).mapTo[SprintEntryEntity]
}

val sprintEntryQuery = TableQuery[SprintEntryTable]

trait SprintService {
  def createSprint(channelId: Long, startedById: Long): Task[SprintEntity]

  def createSprintSection(sprintId: Long, sprintStatus: SprintStatus, duration: Duration, startTime: Instant): Task[SprintSectionEntity]

  def joinSprint(userId: Long, startSectionId: Long, startingWords: Long): Task[SprintEntryEntity]

  def submitCounts(userId: Long, sprintId: Long, endingWords: Long): Task[Boolean]

  def getSprintById(id: Long): Task[Option[(SprintEntity, Seq[SprintSectionEntity], Seq[SprintEntryEntity])]]

  def getActiveSprintByChannelId(channelId: Long): Task[Option[(SprintEntity, Seq[SprintSectionEntity], Seq[SprintEntryEntity])]]
}

object SprintService {
  val live: URLayer[DatabaseZIO, SprintService] = ZLayer.fromFunction(SprintServiceLive(_))
}

case class SprintServiceLive(
  databaseZIO: DatabaseZIO
) extends SprintService {

  override def createSprint(channelId: Long, startedById: Long): Task[SprintEntity] = {
    val newSprint = SprintEntity(0, channelId, startedById)
    databaseZIO.run {
      (sprintQuery returning sprintEntryQuery.map(_.id) into ((sprint, id) => sprint.copy(id = id))) += newSprint
    }
  }

  override def createSprintSection(sprintId: Long, sprintStatus: SprintStatus, duration: Duration, startTime: Instant): Task[SprintSectionEntity] = {
    val newSection = SprintSectionEntity(0, sprintId, sprintStatus, duration, startTime)
    databaseZIO.run {
      (sprintSectionQuery returning sprintSectionQuery.map(_.id) into ((section, id) => section.copy(id = id))) += newSection
    }
  }

  override def joinSprint(userId: Long, startSectionId: Long, startingWords: Long): Task[SprintEntryEntity] = {
    databaseZIO.run {
      implicit _ =>
        for {
          sprintIds <- sprintSectionQuery.filter(_.id === startSectionId).map(_.sprintId).result
          sprintId <- sprintIds.headOption.map(DBIO.successful).getOrElse(DBIO.failed(new IllegalArgumentException("Invalid Section Id")))
          sprintEntry <- (sprintEntryQuery returning sprintEntryQuery) += SprintEntryEntity(0, userId, sprintId, startSectionId, startingWords, None)
        } yield sprintEntry
    }
  }

  override def submitCounts(userId: Long, sprintId: Long, endingWords: Long): Task[Boolean] = {
    for {
      updateCount <- databaseZIO.run {
        sprintEntryQuery.filter(_.userId === userId)
          .filter(_.sprintId === sprintId)
          .map(_.endingWords)
          .update(Some(endingWords))
      }
    } yield updateCount == 1
  }

  override def getSprintById(id: Long): Task[Option[(SprintEntity, Seq[SprintSectionEntity], Seq[SprintEntryEntity])]] = {
    databaseZIO.run { implicit _: ExecutionContext =>
      for {
        sprint <- getSprintByIdDBIO(id)
        sprintEntries <- sprint.map(value => getSprintEntriesForSprintDBIO(value.id))
          .getOrElse(DBIO.successful(Seq()))
        sprintSections <- sprint.map(value => getSprintSectionEntriesForSprintDBIO(value.id))
          .getOrElse(DBIO.successful(Seq()))
      } yield sprint.map((_, sprintSections, sprintEntries))
    }
  }

  override def getActiveSprintByChannelId(channelId: Long): Task[Option[(SprintEntity, Seq[SprintSectionEntity], Seq[SprintEntryEntity])]] = {
    databaseZIO.run { implicit _: ExecutionContext =>
      for {
        sprint <- getActiveSprintByChannelDBIO(channelId)
        sprintEntries <- sprint.map(value => getSprintEntriesForSprintDBIO(value.id))
          .getOrElse(DBIO.successful(Seq()))
        sprintSections <- sprint.map(value => getSprintSectionEntriesForSprintDBIO(value.id))
          .getOrElse(DBIO.successful(Seq()))
      } yield sprint.map((_, sprintSections, sprintEntries))
    }
  }

  private def getSprintByIdDBIO(id: Long): DBIO[Option[SprintEntity]] = {
    sprintQuery.filter(_.id === id)
      .sortBy(_.id)
      .take(1)
      .result
      .headOption
  }

  private def getActiveSprintByChannelDBIO(channelId: Long): DBIO[Option[SprintEntity]] = {
    sprintQuery.filter(_.channelId === channelId)
      .sortBy(_.id)
      .take(1)
      .result
      .headOption
  }

  private def getSprintEntriesForSprintDBIO(sprintId: Long): DBIO[Seq[SprintEntryEntity]] = {
    sprintEntryQuery.filter(_.sprintId === sprintId)
      .result
  }

  private def getSprintSectionEntriesForSprintDBIO(sprintId: Long): DBIO[Seq[SprintSectionEntity]] = {
    sprintSectionQuery.filter(_.sprintId === sprintId)
      .result
  }
}
