package io.sommers.zio.twitch
package server

import model.webhook.Subscription
import model.webhook.event.TwitchEventType
import server.TwitchMessageType

import zio.json.ast.Json
import zio.{IO, URLayer, ZIO, ZLayer}

trait TwitchMessageHandler {
  def handleMessage(messageType: TwitchMessageType, json: Json): IO[Throwable, Either[String, Unit]]
}

case class TwitchMessageHandlerImpl(notificationHandler: TwitchNotificationHandler) extends TwitchMessageHandler {

  override def handleMessage(messageType: TwitchMessageType, json: Json): IO[Throwable, Either[String, Unit]] = {
    for {
      jsonObject <- ZIO.fromOption(json.asObject)
        .orElseFail(new IllegalArgumentException("json was not object"))
      result <- messageType match {
        case TwitchMessageType.Verification => handleVerification(jsonObject).map(Left(_))
        case TwitchMessageType.Notification => handleNotification(jsonObject).map(Right(_))
        case TwitchMessageType.Revocation => ZIO.succeed(Right(()))
      }
    } yield result
  }

  private def handleVerification(json: Json.Obj): IO[Throwable, String] = {
    for {
      challengeJson <- ZIO.fromOption(json.get("challenge"))
        .orElseFail(new IllegalArgumentException("challenge is missing from verification"))
      challenge <- ZIO.fromOption(challengeJson.asString)
        .orElseFail(new IllegalArgumentException("challenge was not a string"))
    } yield challenge
  }

  private def handleNotification(obj: Json.Obj): IO[Throwable, Unit] = {
    for {
      subscriptionJson <- ZIO.fromOption(obj.get("subscription"))
        .orElseFail(new IllegalArgumentException(".subscription(missing)"))
      subscription <- ZIO.fromEither(subscriptionJson.as[Subscription])
        .mapError(jsonError => new IllegalArgumentException(jsonError))
      eventJson <- ZIO.fromOption(obj.get("event"))
        .orElseFail(new IllegalArgumentException(".event(missing)"))
      event <- TwitchEventType.parse(subscription.eventType, eventJson)
        .mapError(string => new IllegalArgumentException(s".event(Parse fail $string)"))
      _ <- notificationHandler.handleNotification(subscription, event)
    } yield ()
  }
}

object TwitchMessageHandler {
  val live: URLayer[TwitchNotificationHandler, TwitchMessageHandler] = ZLayer.fromFunction(TwitchMessageHandlerImpl(_))
}
