package io.sommers.aiintheipaw
package service

import database.CamelCaseNoEntitySqlNameMapper
import model.sprint.SprintStatus
import model.sprint.SprintStatus.Unknown
import service.SprintCodecs.given_DbCodec_SprintStatus

import com.augustnagro.magnum.*
import com.augustnagro.magnum.magzio.TransactorZIO
import com.augustnagro.magnum.pg.PgCodec.given
import org.postgresql.util.PGInterval
import zio.{Duration, Task, URLayer, ZLayer}

import java.sql.Timestamp
import java.time.Instant
import scala.annotation.unused
import scala.util.Try


@unused
object SprintCodecs:
  given DbCodec[SprintStatus] = DbCodec[String]
    .biMap(
      name => Try(SprintStatus.valueOf(name))
        .getOrElse(Unknown),
      _.toString
    )


@Table(PostgresDbType, CamelCaseNoEntitySqlNameMapper)
case class SprintEntity(
  @Id id: Long,
  channelId: Long,
  startedByUserId: Long,
)derives DbCodec

@Table(PostgresDbType, CamelCaseNoEntitySqlNameMapper)
case class CreateSprintEntity(
  channelId: Long,
  startedByUserId: Long,
)derives DbCodec

@Table(PostgresDbType, CamelCaseNoEntitySqlNameMapper)
case class SprintSectionEntity(
  @Id id: Long,
  sprintId: Long,
  status: SprintStatus,
  totalTime: PGInterval,
  startTime: Timestamp
)derives DbCodec

@Table(PostgresDbType, CamelCaseNoEntitySqlNameMapper)
case class CreateSprintSectionEntity(
  sprintId: Long,
  status: SprintStatus,
  totalTime: PGInterval,
  startTime: Timestamp
)derives DbCodec

object CreateSprintSectionEntity {
  def apply(sprintId: Long, status: SprintStatus, totalTime: Duration, startTime: Instant): CreateSprintSectionEntity = CreateSprintSectionEntity(
    sprintId,
    status,
    new PGInterval(0, 0, totalTime.toDays.toInt, totalTime.toHours.toInt, totalTime.toMinutes.toInt, totalTime.toSeconds.toInt),
    Timestamp.from(startTime)
  )
}

@Table(PostgresDbType, CamelCaseNoEntitySqlNameMapper)
case class SprintEntryEntity(
  @Id id: Long,
  userId: Long,
  sprintId: Long,
  startSectionId: Long,
  startingWords: Long,
  endingWords: Option[Long],
  timeRemaining: PGInterval
)derives DbCodec

@Table(PostgresDbType, CamelCaseNoEntitySqlNameMapper)
case class CreateSprintEntryEntity(
  userId: Long,
  sprintId: Long,
  startSectionId: Long,
  startingWords: Long,
  endingWords: Option[Long],
  timeRemaining: PGInterval
)derives DbCodec

trait SprintService {
  def createSprint(createSprintEntity: CreateSprintEntity): Task[SprintEntity]

  def updateSprintStatus(createSprintStatusEntity: CreateSprintSectionEntity): Task[SprintSectionEntity]

  def joinSprint(createSprintEntryEntity: CreateSprintEntryEntity): Task[SprintEntryEntity]

  def getSprintById(id: Long): Task[Option[(SprintEntity, Seq[SprintSectionEntity], Seq[SprintEntryEntity])]]

  def getActiveSprintByChannelId(channelId: Long): Task[Option[(SprintEntity, Seq[SprintSectionEntity], Seq[SprintEntryEntity])]]
}

object SprintService {
  val live: URLayer[TransactorZIO, SprintService] = ZLayer.fromFunction(SprintServiceLive(_))
}

case class SprintServiceLive(
  transactorZIO: TransactorZIO
) extends SprintService {

  private val sprintRepo = Repo[CreateSprintEntity, SprintEntity, Long]
  private val sprintEntryRepo = Repo[CreateSprintEntryEntity, SprintEntryEntity, Long]
  private val sprintStatusRepo = Repo[CreateSprintSectionEntity, SprintSectionEntity, Long]

  override def createSprint(createSprintEntity: CreateSprintEntity): Task[SprintEntity] = {
    transactorZIO.connect:
      sprintRepo.insertReturning(createSprintEntity)
  }

  override def updateSprintStatus(createSprintStatusEntity: CreateSprintSectionEntity): Task[SprintSectionEntity] = {
    transactorZIO.connect:
      sprintStatusRepo.insertReturning(createSprintStatusEntity)
  }

  override def joinSprint(createSprintEntryEntity: CreateSprintEntryEntity): Task[SprintEntryEntity] = {
    transactorZIO.connect:
      sprintEntryRepo.insertReturning(createSprintEntryEntity)
  }

  override def getSprintById(id: Long): Task[Option[(SprintEntity, Seq[SprintSectionEntity], Seq[SprintEntryEntity])]] = {
    transactorZIO.connect:
      for {
        sprintEntity <- sprintRepo.findById(id)
      } yield (
        sprintEntity,
        sprintStatusRepo.findAll(Spec[SprintSectionEntity].where(sql"sprint_id = $id")),
        sprintEntryRepo.findAll(Spec[SprintEntryEntity].where(sql"sprint_id = $id"))
      )
  }

  override def getActiveSprintByChannelId(channelId: Long): Task[Option[(SprintEntity, Seq[SprintSectionEntity], Seq[SprintEntryEntity])]] = {
    transactorZIO.connect {
      db ?=>
        for {
          sprintEntity <- sprintRepo.findAll(Spec[SprintEntity]
            .where(sql"channel_id = $channelId")
            .limit(1)
            .orderBy("id", SortOrder.Desc)
          ).headOption
        } yield (
          sprintEntity,
          sprintStatusRepo.findAll(Spec[SprintSectionEntity]
            .where(sql"sprint_id = ${sprintEntity.id}")
            .orderBy("id", SortOrder.Desc)
          ),
          sprintEntryRepo.findAll(Spec[SprintEntryEntity].where(sql"sprint_id = ${sprintEntity.id}"))
        )
    }
  }
}
