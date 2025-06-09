package io.sommers.zio.twitch
package server

import model.webhook.Subscription
import model.webhook.event.TwitchEventType
import server.TwitchMessageType.TwitchMessageType

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
        .mapError(_ => new IllegalArgumentException(".subscription(missing)"))
      subscription <- subscriptionJson.as[Subscription]
        .fold(
          jsonError => ZIO.fail(new IllegalArgumentException(jsonError)),
          {
            subscription: Subscription => ZIO.succeed(subscription)
          }
        )
      eventJson <- obj.get("event")
        .fold[ZIO[Any, Throwable, Json]](ZIO.fail(new IllegalArgumentException(".event(missing)"))) {
          json: Json => ZIO.succeed(json)
        }
      event <- TwitchEventType.parse(subscription.eventType, eventJson)
        .mapError(string => new IllegalArgumentException(s".event(Parse fail $string)"))
      _ <- notificationHandler.handleNotification(subscription, event)
    } yield ()
  }
}

object TwitchMessageHandler {
  val live: URLayer[TwitchNotificationHandler, TwitchMessageHandler] = ZLayer.fromFunction(TwitchMessageHandlerImpl(_))
}
