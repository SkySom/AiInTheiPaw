package io.sommers.zio.twitch
package server

import model.webhook.WebhookRequest
import server.TwitchMessageType.TwitchMessageType

import zio.http.endpoint.AuthType.None
import zio.http.endpoint.Endpoint
import zio.http.{Handler, Response, Route, RoutePattern, Status}
import zio.{ZNothing, http}

object TwitchWebHook {
  //noinspection ScalaWeakerAccess
  def endpoint(path: String): Endpoint[Unit, (TwitchMessageId, TwitchMessageType, TwitchMessageTimestamp, TwitchMessageSignature, WebhookRequest), ZNothing, Either[String, Unit], None] =
    Endpoint(RoutePattern.POST / path)
      .header(TwitchMessageId.codec)
      .header(TwitchMessageType.codec)
      .header(TwitchMessageTimestamp.codec)
      .header(TwitchMessageSignature.codec)
      .in[WebhookRequest]
      .out[Unit](Status.NoContent)
      .out[String](Status.Ok)

  def route(path: String): Route[Any, Response] = route(endpoint(path))

  //noinspection ScalaWeakerAccess
  def route(endpoint: Endpoint[Unit, (TwitchMessageId, TwitchMessageType, TwitchMessageTimestamp, TwitchMessageSignature, WebhookRequest), ZNothing, Either[String, Unit], None]): Route[Any, Nothing] = endpoint.implementHandler(handler)

  val handler: Handler[Any, ZNothing, (TwitchMessageId, TwitchMessageType, TwitchMessageTimestamp, TwitchMessageSignature, WebhookRequest), Either[String, Unit]] =
    http.handler((id: TwitchMessageId, messageType: TwitchMessageType, timestamp: TwitchMessageTimestamp, signature: TwitchMessageSignature, request: WebhookRequest) => {
      Left("")
    })
}
