package io.sommers.zio.twitch
package server

import util.TwitchSignatureVerifier

import zio.http.Request
import zio.http.codec.{HeaderCodec, HttpCodecError}
import zio.schema.Schema
import zio.schema.validation.Validation
import zio.{IO, UIO, ZIO}

import java.nio.charset.StandardCharsets
import java.time.{Duration, Instant}


case class TwitchMessageId(value: String) {

}

object TwitchMessageId {
  implicit val schema: Schema[TwitchMessageId] = Schema.primitive[String]
    .validation(Validation.minLength(1))
    .transform(TwitchMessageId(_), _.value)

  val codec: HeaderCodec[TwitchMessageId] = HeaderCodec.headerAs("twitch-eventsub-message-id")

  def fromRequest(request: Request): IO[HttpCodecError.HeaderError, TwitchMessageId] = request.headerZIO("twitch-eventsub-message-id")

}

enum TwitchMessageType(val name: String) {
  case Verification extends TwitchMessageType("webhook_callback_verification")
  case Notification extends TwitchMessageType("notification")
  case Revocation extends TwitchMessageType("revocation")
}

object TwitchMessageType {
  implicit val schema: Schema[TwitchMessageType] = Schema.primitive[String]
    .transformOrFail(
      {
        case Verification.name => Right(Verification)
        case Notification.name => Right(Notification)
        case Revocation.name => Right(Revocation)
        case other => Left(s"$other is not a valid message type")
      },
      value => Right(value.name)
    )
    
  val codec: HeaderCodec[TwitchMessageType] = HeaderCodec.headerAs("twitch-eventsub-message-type")

  def fromRequest(request: Request): IO[HttpCodecError.HeaderError, TwitchMessageType] = request.headerZIO("twitch-eventsub-message-type")
}

case class TwitchMessageTimestamp(timestamp: Instant) {
  def isValid(maxDuration: Duration): UIO[Boolean] = {
    for {
      clock <- ZIO.clock
      current <- clock.instant
      duration <- ZIO.succeed(Duration.between(timestamp, current))
    } yield duration.isPositive && maxDuration.compareTo(duration) < 0
  }
}

object TwitchMessageTimestamp {
  implicit val schema: Schema[TwitchMessageTimestamp] = Schema.primitive[Instant].transform(TwitchMessageTimestamp(_), _.timestamp)

  val codec: HeaderCodec[TwitchMessageTimestamp] = HeaderCodec.headerAs("twitch-eventsub-message-timestamp")

  def fromRequest(request: Request): IO[HttpCodecError.HeaderError, TwitchMessageTimestamp] = request.headerZIO("twitch-eventsub-message-timestamp")
}

case class TwitchMessageSignature(signature: String) {
  def validate(secret: String, messageId: TwitchMessageId, messageTimestamp: TwitchMessageTimestamp, bodyString: String): ZIO[TwitchSignatureVerifier, Throwable, Boolean] =
    ZIO.serviceWithZIO[TwitchSignatureVerifier](
      _.verifySignature(
        secret,
        messageId.value,
        messageTimestamp.timestamp,
        bodyString.getBytes(StandardCharsets.UTF_8),
        signature
      )
    )
}

object TwitchMessageSignature {
  implicit val schema: Schema[TwitchMessageSignature] = Schema.primitive[String].validation(Validation.minLength(1))
    .transform(TwitchMessageSignature(_), _.signature)

  val codec: HeaderCodec[TwitchMessageSignature] = HeaderCodec.headerAs("twitch-eventsub-message-signature")

  def fromRequest(request: Request): IO[HttpCodecError.HeaderError, TwitchMessageSignature] = request.headerZIO("twitch-eventsub-message-signature")

}