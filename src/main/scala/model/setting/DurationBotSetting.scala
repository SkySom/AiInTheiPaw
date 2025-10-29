package io.sommers.aiintheipaw
package model.setting

import model.problem.{Problem, ThrowableProblem}

import zio.{Duration, IO, ZIO}

import java.time.Duration as JavaDuration

case class DurationBotSetting(
  override val key: String
) extends BotSetting[Duration] {
  override def readFrom(value: String): IO[Problem, Duration] = {
    ZIO.attempt(Duration.fromJava(JavaDuration.parse(value)))
      .mapError(ThrowableProblem(_))
  }

  override def writeTo(value: Duration): String = {
    value.toString
  }
}
