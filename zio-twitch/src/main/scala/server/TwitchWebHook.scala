package io.sommers.zio.twitch
package server

import model.webhook.WebhookRequest
import server.TwitchMessageType.TwitchMessageType

import io.sommers.zio.twitch.model.webhook.event.ChannelChatMessage
import zio.http.endpoint.AuthType.None
import zio.http.endpoint.Endpoint
import zio.http.{Body, Handler, Headers, Method, Request, Response, Route, RoutePattern, Status}
import zio.schema.NameFormat.SnakeCase
import zio.schema.codec.BinaryCodec
import zio.schema.codec.JsonCodec.{Configuration, schemaBasedBinaryCodec}
import zio.{ZNothing, http}

object TwitchWebHook {
  //noinspection ScalaWeakerAccess
  def endpoint(path: String): Endpoint[Unit, (TwitchMessageId, TwitchMessageType, TwitchMessageTimestamp, TwitchMessageSignature, WebhookRequest[ChannelChatMessage]), ZNothing, Either[String, Unit], None] =
    Endpoint(RoutePattern.POST / path)
      .header(TwitchMessageId.codec)
      .header(TwitchMessageType.codec)
      .header(TwitchMessageTimestamp.codec)
      .header(TwitchMessageSignature.codec)
      .in[WebhookRequest[ChannelChatMessage]]
      .out[Unit](Status.NoContent)
      .out[String](Status.Ok)

  def route(path: String): Route[Any, Response] = Method.POST / path -> newHandler

  //noinspection ScalaWeakerAccess
  def route(endpoint: Endpoint[Unit, (TwitchMessageId, TwitchMessageType, TwitchMessageTimestamp, TwitchMessageSignature, WebhookRequest[ChannelChatMessage]), ZNothing, Either[String, Unit], None]): Route[Any, Nothing] = endpoint.implementHandler(handler)

  val handler: Handler[Any, ZNothing, (TwitchMessageId, TwitchMessageType, TwitchMessageTimestamp, TwitchMessageSignature, WebhookRequest[ChannelChatMessage]), Either[String, Unit]] =
    http.handler((id: TwitchMessageId, messageType: TwitchMessageType, timestamp: TwitchMessageTimestamp, signature: TwitchMessageSignature, request: WebhookRequest[ChannelChatMessage]) => {
      Left("")
    })

  implicit val decoded: BinaryCodec[WebhookRequest[ChannelChatMessage]] = schemaBasedBinaryCodec[WebhookRequest[ChannelChatMessage]](Configuration(fieldNameFormat = SnakeCase))
  private val newHandler: Handler[Any, ZNothing, Request, Response] =
    Handler.fromFunctionZIO((request: Request) => {
      request.body.to[WebhookRequest[ChannelChatMessage]].fold(
        failure => Response.fromThrowable(failure),
        success => Response(Status.Ok, Headers(), Body.from(success))
      )
    })
}
