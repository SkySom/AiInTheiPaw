package io.sommers.zio.twitch
package server

import model.webhook.Subscription
import model.webhook.event.TwitchEvent

import zio.IO

trait TwitchNotificationHandler {
  def handleNotification[TE](subscription: Subscription, event: TE): IO[Throwable, Unit]
}