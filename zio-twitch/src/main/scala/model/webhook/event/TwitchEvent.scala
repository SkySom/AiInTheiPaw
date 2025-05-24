package io.sommers.zio.twitch
package model.webhook.event

trait TwitchEvent[U <: TwitchEvent[U]] {
  def twitchEventType: TwitchEventType[U]
}
