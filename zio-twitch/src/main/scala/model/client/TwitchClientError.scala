package io.sommers.zio.twitch
package model.client

import scala.annotation.tailrec

case class TwitchClientError(
  message: String,
  exception: Option[Throwable] = None
) {

}

object TwitchClientError {
  def apply(message: String, exception: Throwable): TwitchClientError = TwitchClientError(message, Some(exception))

  def apply(throwable: Throwable): TwitchClientError = TwitchClientError(throwable.getMessage, throwable)

  def apply(value: Any): TwitchClientError = value match {
    case throwable: Throwable => TwitchClientError(s"Encountered Exception: ${throwable.getMessage}", throwable)
    case twitchClientError: TwitchClientError => twitchClientError
    case other => TwitchClientError(s"Encountered unknown error value: ${other.toString}")
  }
}
