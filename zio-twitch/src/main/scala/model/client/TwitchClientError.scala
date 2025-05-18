package io.sommers.zio.twitch
package model.client

case class TwitchClientError(
  message: String,
  exception: Option[Throwable] = None
) {

}

object TwitchClientError {
  def apply(message: String, exception: Throwable): TwitchClientError = TwitchClientError(message, Some(exception))

  def apply(throwable: Throwable): TwitchClientError = TwitchClientError(throwable.getMessage, throwable)
}
