package io.sommers.aiintheipaw
package logic

import event.EventScheduler
import logic.message.MessageLogic
import model.channel.Channel
import model.channel.setting.DurationChannelSetting
import model.problem.{InvalidValueProblem, Problem, ThrowableProblem}
import model.sprint.{Sprint, SprintStatus}
import model.user.User
import util.Enrichment.EnrichOption

import io.sommers.zio.localize.Localizer
import zio.config.magnolia.*
import zio.{Config, Duration, Layer, URLayer, ZIO, ZLayer}

import java.util.Locale
import scala.math.Ordered.orderingToOrdered

trait SprintCommandLogic {
  def createSprint(channel: Channel, user: User, replyTo: Option[String], inProgressDuration: Option[Duration]): ZIO[EventScheduler, Problem, Sprint]

  def progressSprint(sprintId: Long, currentSectionId: Long, nextSectionStatus: SprintStatus): ZIO[EventScheduler, Problem, Unit]
}

case class SprintCommandLogicLive(
  sprintLogic: SprintLogic,
  sprintConfig: SprintConfig,
  channelSettingLogic: ChannelSettingLogic,
  messageLogic: MessageLogic,
  localizer: Localizer
) extends SprintCommandLogic {
  override def createSprint(channel: Channel, user: User, replyTo: Option[String], inProgressDurationArg: Option[Duration]): ZIO[EventScheduler, Problem, Sprint] = for {
    maxInProgressDuration <- channelSettingLogic.getValue(channel, SprintConfig.maxInProgressSetting, sprintConfig.maxInProgressDuration)
    signUpDuration <- channelSettingLogic.getValue(channel, SprintConfig.signUpSetting, sprintConfig.signUpDuration)
    inProgressDuration <- inProgressDurationArg.orElseZIO(channelSettingLogic.getValue(channel, SprintConfig.inProgressSetting, sprintConfig.inProgressDuration))
      .flatMap(duration =>
        if (duration > maxInProgressDuration) {
          ZIO.fail(InvalidValueProblem(s"$duration is greater than $maxInProgressDuration"))
        } else {
          ZIO.succeed(duration)
        }
      )
    sprint <- sprintLogic.createSprint(channel, user, signUpDuration, inProgressDuration)
    message <- localizer.localize(Locale.US, "sprint.created", signUpDuration, inProgressDuration)
      .mapError(ThrowableProblem(_))
    _ <- messageLogic.sendMessage(channel, message)
  } yield sprint

  override def progressSprint(sprintId: Long, currentSectionId: Long, nextSectionStatus: SprintStatus): ZIO[EventScheduler, Problem, Unit] = {
    for {
      sprint <- sprintLogic.getSprintById(sprintId)
      nextSection <- sprintLogic.progressSprint(currentSectionId, nextSectionStatus)
      message <- localizer.localize(Locale.US, s"sprint.section.${nextSection.status}")
        .mapError(Problem(_))
      _ <- messageLogic.sendMessage(sprint.channel, message)
    } yield ()
  }
}

object SprintCommandLogic {
  val live: URLayer[SprintLogic & SprintConfig & ChannelSettingLogic & MessageLogic & Localizer, SprintCommandLogic] = 
    ZLayer.fromFunction(SprintCommandLogicLive.apply)
}

case class SprintConfig(
  signUpDuration: Duration,
  inProgressDuration: Duration,
  awaitingCountsDuration: Duration,
  maxInProgressDuration: Duration
) derives Config

object SprintConfig {
  val live: Layer[Config.Error, SprintConfig] = ZLayer.fromZIO(
    ZIO.configProviderWith(_.nested("sprint")
      .load[SprintConfig]
    )
  )

  val signUpSetting: DurationChannelSetting = DurationChannelSetting("sprint.sign_up.duration")
  val inProgressSetting: DurationChannelSetting = DurationChannelSetting("sprint.in_progress.duration")
  val awaitingCountsSetting: DurationChannelSetting = DurationChannelSetting("sprint.awaiting_counts.duration")
  val maxInProgressSetting: DurationChannelSetting = DurationChannelSetting("sprint.in_progress.max_duration")

}