package io.sommers.aiintheipaw
package model.sprint

import model.channel.Channel
import model.user.User

import zio.Duration

import java.time.Instant

case class Sprint(
  id: Long,
  channel: Channel,
  startedBy: User,
  sections: Seq[SprintSection],
  entries: Seq[SprintEntry]
)

case class SprintSection(
  id: Long,
  status: SprintStatus,
  startTime: Instant,
  duration: Duration
)

case class SprintEntry(
  id: Long,
  user: User,
  startSectionId: Long,
  startingWords: Long,
  endingWords: Option[Long]
)