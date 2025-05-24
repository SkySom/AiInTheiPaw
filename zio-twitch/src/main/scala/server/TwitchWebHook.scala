package io.sommers.zio.twitch
package server

import model.webhook.WebhookRequest
import model.webhook.event.ChannelChatMessage

import io.sommers.zio.twitch.client.TwitchRestClientConfig
import zio.config.magnolia.DeriveConfig
import zio.{&, Config, Layer, ZIO, ZLayer}
import zio.http.{Body, Handler, Headers, Method, Request, Response, Route, Status}
import zio.json.ast.Json
import zio.schema.NameFormat.SnakeCase
import zio.schema.codec.BinaryCodec
import zio.schema.codec.JsonCodec.{Configuration, schemaBasedBinaryCodec}
import zio.schema.codec.json._

object TwitchWebHook {
  //noinspection ScalaWeakerAccess
  /*def endpoint(path: String): Endpoint[Unit, (TwitchMessageId, TwitchMessageType, TwitchMessageTimestamp, TwitchMessageSignature, WebhookRequest[ChannelChatMessage]), ZNothing, Either[String, Unit], None] =
    Endpoint(RoutePattern.POST / path)
      .header(TwitchMessageId.codec)
      .header(TwitchMessageType.codec)
      .header(TwitchMessageTimestamp.codec)
      .header(TwitchMessageSignature.codec)
      .in[WebhookRequest[ChannelChatMessage]]
      .out[Unit](Status.NoContent)
      .out[String](Status.Ok)*/

  def route(path: String): Route[TwitchWebHookConfig & TwitchMessageHandler, Response] = Method.POST / path -> newHandler

  implicit val decoded: BinaryCodec[WebhookRequest[ChannelChatMessage]] = schemaBasedBinaryCodec[WebhookRequest[ChannelChatMessage]](Configuration(fieldNameFormat = SnakeCase))
  private val newHandler: Handler[TwitchWebHookConfig & TwitchMessageHandler, Response, Request, Response] =
    Handler.fromFunctionZIO(
      (request: Request) => {
        (for {
          messageId <- TwitchMessageId.fromRequest(request)
            .mapError(headerError => headerError.getCause)
          messageType <- TwitchMessageType.fromRequest(request)
            .mapError(headerError => headerError.getCause)
          timestamp <- TwitchMessageTimestamp.fromRequest(request)
            .mapError(headerError => headerError.getCause)
          signature <- TwitchMessageSignature.fromRequest(request)
            .mapError(headerError => headerError.getCause)
          webhookSecret <- ZIO.serviceWith[TwitchWebHookConfig](_.secret)
          bodyString <- request.body.asString
          _ <- signature.validate(webhookSecret, messageId, timestamp, bodyString)
            .flatMap(valid => if (valid) ZIO.succeed(()) else ZIO.fail(new IllegalStateException("Signatures did not match")))
          json <- request.body.to[Json]
          handleMessage <- ZIO.serviceWithZIO[TwitchMessageHandler](_.handleMessage(messageType, json))
        } yield Response(Status.Ok, Headers(), handleMessage.fold(Body.fromString(_), _ => Body.empty)))
          .catchAll(throwable => ZIO.log(throwable.getMessage).map(_ => Response.fromThrowable(throwable)))
      }
    )
}

case class TwitchWebHookConfig(
  secret: String
)

object TwitchWebHookConfig {
  implicit val config: Config[TwitchWebHookConfig] = DeriveConfig.deriveConfig[TwitchWebHookConfig]

  val live: Layer[Config.Error, TwitchWebHookConfig] = ZLayer.fromZIO(ZIO.configProviderWith(_.nested("twitch.webhook")
    .load[TwitchWebHookConfig]
  ))
}
