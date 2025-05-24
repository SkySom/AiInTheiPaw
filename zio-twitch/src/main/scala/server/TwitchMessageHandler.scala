package io.sommers.zio.twitch
package server

import model.webhook.Subscription
import model.webhook.event.{TwitchEvent, TwitchEventType}
import server.TwitchMessageType.TwitchMessageType

import zio.json.JsonDecoder
import zio.json.ast.Json
import zio.{IO, URLayer, ZIO, ZLayer}

trait TwitchMessageHandler {
  def handleMessage(messageType: TwitchMessageType, json: Json): IO[Throwable, Either[String, Unit]]
}

case class TwitchMessageHandlerImpl(notificationHandler: TwitchNotificationHandler) extends TwitchMessageHandler {

  override def handleMessage(messageType: TwitchMessageType, json: Json): IO[Throwable, Either[String, Unit]] = {
    for {
      jsonObject <- ZIO.fromOption(json.asObject)
        .mapError(_ => new IllegalArgumentException("json was not object"))
      result <- messageType match {
        case TwitchMessageType.VERIFICATION => handleVerification(jsonObject).map(Left(_))
        case TwitchMessageType.NOTIFICATION => handleNotification(jsonObject).map(Right(_))
        case TwitchMessageType.REVOCATION => ZIO.succeed(Right(()))
      }
    } yield result
  }

  private def handleVerification(json: Json.Obj): IO[Throwable, String] = {
    for {
      challengeJson <- ZIO.fromOption(json.get("challenge"))
        .mapError(_ => new IllegalArgumentException("challenge is missing from verification"))
      challenge <- ZIO.fromOption(challengeJson.asString)
        .mapError(_ => new IllegalArgumentException("challenge was not a string"))
    } yield challenge
  }

  private def handleNotification(obj: Json.Obj): IO[Throwable, Unit] = {
    for {
      subscriptionJson <- ZIO.fromOption(obj.get("subscription"))
        .mapError(_ => new IllegalArgumentException("subscription is missing from notification"))
      subscription <- ZIO.fromEither(subscriptionJson.as[Subscription])
        .mapError(jsonError => new IllegalArgumentException(jsonError))
      eventJson <- ZIO.fromOption(obj.get("event"))
        .mapError(_ => new IllegalArgumentException("event is missing from notification"))
      eventType <- TwitchEventType.get(subscription.eventType)
      event <- ZIO.fromEither(eventJson.as[TwitchEvent[_]](eventType.jsonDecoder.asInstanceOf[JsonDecoder[TwitchEvent[_]]]))
        .mapError(jsonError => new IllegalArgumentException(jsonError))
      _ <- notificationHandler.handleNotification(subscription, event)
    } yield ()
  }
}

object TwitchMessageHandler {
  val live: URLayer[TwitchNotificationHandler, TwitchMessageHandler] = ZLayer.fromFunction(TwitchMessageHandlerImpl(_))
}
