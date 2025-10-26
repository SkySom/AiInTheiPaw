package io.sommers.aiintheipaw
package model.channel.setting

import model.problem.Problem

import zio.IO

trait ChannelSetting[T] {
  val key: String

  def readFrom(value: String): IO[Problem, T]

  def writeTo(value: T): String
}
