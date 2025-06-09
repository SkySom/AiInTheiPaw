package io.sommers.aiintheipaw
package model.service

sealed trait Service {
  val name: String
}

object TwitchService extends Service {
  override val name: String = "Twitch"
}