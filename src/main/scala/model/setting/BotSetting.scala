package io.sommers.aiintheipaw
package model.setting

import model.problem.Problem

import zio.IO

trait BotSetting[T] {
  val key: String

  def readFrom(value: String): IO[Problem, T]

  def writeTo(value: T): String
}
