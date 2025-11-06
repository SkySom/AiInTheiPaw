package io.sommers.aiintheipaw
package mock.service

import model.sprint.SprintStatus
import service.{SprintEntity, SprintEntryEntity, SprintSectionEntity, SprintService}

import zio.{Duration, Task, ULayer, ZIO, ZLayer}

import java.time.Instant
import scala.collection.mutable

class SprintServiceMock extends SprintService {
  val sprints: mutable.Map[Long, (SprintEntity, mutable.ArrayBuffer[SprintSectionEntity], mutable.ArrayBuffer[SprintEntryEntity])] = new mutable.HashMap()

  override def createSprint(channelId: Long, startedById: Long, progressDuration: Duration): Task[SprintEntity] = {
    if (sprints.values.exists(sprint => sprint._1.channelId == channelId && !sprint._2.exists(_.status.active))) {
      ZIO.fail(new IllegalStateException("Already Exists"))
    } else {
      val id = sprints.keys.maxOption.getOrElse(1L)
      val sprint = (SprintEntity(id, channelId, startedById, progressDuration), mutable.ArrayBuffer[SprintSectionEntity](), mutable.ArrayBuffer[SprintEntryEntity]())
      sprints.put(id, sprint)
      ZIO.succeed(sprint._1)
    }
  }

  override def createSprintSection(sprintId: Long, sprintStatus: SprintStatus, duration: Duration, startTime: Instant): Task[SprintSectionEntity] = {
    sprints.get(sprintId)
      .fold(ZIO.fail(new IllegalStateException(s"No Sprint exists for $sprintId"))) {
        sprint => {
          val sectionId = sprints.values.map(_._2.map(_.id).maxOption.getOrElse(0L)).max
          val section = SprintSectionEntity(sectionId, sprintId, sprintStatus, duration, startTime)
          sprint._2.append(section)
          ZIO.succeed(section)
        }
      }
  }

  override def joinSprint(userId: Long, startSectionId: Long, startingWords: Long): Task[SprintEntryEntity] = {
    sprints.find(_._2._2.exists(_.id == startSectionId))
      .fold(ZIO.fail(new IllegalStateException(s"No Sprint Section exists for $startSectionId"))) {
        sprint => {
          val entryId = sprints.values.map(_._3.map(_.id).maxOption.getOrElse(0L)).max
          val entry = SprintEntryEntity(entryId, userId, sprint._2._1.id, startSectionId, startingWords, None)

          sprint._2._3.append(entry)
          ZIO.succeed(entry)
        }
      }
  }

  override def submitCounts(userId: Long, sprintId: Long, endingWords: Long): Task[Boolean] = ???

  override def getSprintById(id: Long): Task[Option[(SprintEntity, Seq[SprintSectionEntity], Seq[SprintEntryEntity])]] =
    sprints.get(id)
      .fold(ZIO.succeed(None)) {
        sprint => ZIO.succeed(Some(sprint._1, sprint._2.toSeq, sprint._3.toSeq))
      }

  override def getActiveSprintByChannelId(channelId: Long): Task[Option[(SprintEntity, Seq[SprintSectionEntity], Seq[SprintEntryEntity])]] =
    ZIO.succeed(sprints.find(sprint => sprint._2._1.channelId == channelId && sprint._2._2.lastOption.forall(_.status.active))
      .map(_._2)
      .map(sprint => (sprint._1, sprint._2.toSeq, sprint._3.toSeq))
    )

  override def getSprintBySectionId(id: Long): Task[Option[(SprintEntity, Seq[SprintSectionEntity], Seq[SprintEntryEntity])]] = {
    ZIO.succeed(sprints.find(sprint => sprint._2._2.exists(_.id == id))
      .map(_._2)
      .map(sprint => (sprint._1, sprint._2.toSeq, sprint._3.toSeq))
    )
  }
}

object SprintServiceMock {
  val mock: ULayer[SprintServiceMock] = ZLayer.succeed(new SprintServiceMock)
}
