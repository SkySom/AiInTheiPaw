package io.sommers.zio.twitch
package server

import util.TwitchSignatureVerifier

import zio.config.magnolia
import zio.config.magnolia.{DeriveConfig, deriveConfig}
import zio.http.{Body, Handler, Headers, Method, Request, Response, Route, Status}
import zio.json.ast.Json
import zio.schema.codec.JsonCodec.schemaBasedBinaryCodec
import zio.schema.codec.json.*
import zio.{Config, Layer, URLayer, ZEnvironment, ZIO, ZLayer}

case class TwitchWebHookRoutes(
  twitchWebHookConfig: TwitchWebHookConfig,
  twitchMessageHandler: TwitchMessageHandler,
  twitchSignatureVerifier: TwitchSignatureVerifier
) {
  def routes: Seq[Route[Any, Response]] = Seq(webhookRoute)

  private def webhookRoute: Route[Any, Response] = Method.POST / twitchWebHookConfig.route -> newHandler

  private val newHandler: Handler[Any, Response, Request, Response] =
    Handler.fromFunctionZIO(
      (request: Request) => {
        (for {
          messageId <- TwitchMessageId.fromRequest(request)
          messageType <- TwitchMessageType.fromRequest(request)
          timestamp <- TwitchMessageTimestamp.fromRequest(request)
          signature <- TwitchMessageSignature.fromRequest(request)
          bodyString <- request.body.asString
          _ <- signature.validate(twitchWebHookConfig.secret, messageId, timestamp, bodyString)
            .provideEnvironment(ZEnvironment(twitchSignatureVerifier))
            .flatMap(valid => if (valid) ZIO.succeed(()) else ZIO.fail(new IllegalStateException("Signatures did not match")))
          json <- request.body.to[Json]
          handleMessage <- twitchMessageHandler.handleMessage(messageType, json)
        } yield Response(Status.Ok, Headers(), handleMessage.fold(Body.fromString(_), _ => Body.empty)))
          .catchAll(throwable => ZIO.logError(throwable.getMessage)
            .map(_ => Response(Status.InternalServerError, Headers(), Body.fromString(throwable.getMessage)))
          )
      }
    )
}

object TwitchWebHookRoutes {
  val live: URLayer[TwitchWebHookConfig & TwitchMessageHandler & TwitchSignatureVerifier, TwitchWebHookRoutes] =
    ZLayer.fromFunction(TwitchWebHookRoutes(_, _, _))
}

case class TwitchWebHookConfig(
  secret: String,
  route: String = "twitch/callback",
  verify: Boolean = true
)

object TwitchWebHookConfig {
  implicit val config: Config[TwitchWebHookConfig] = deriveConfig[TwitchWebHookConfig]

  val live: Layer[Config.Error, TwitchWebHookConfig] = ZLayer.fromZIO(
    ZIO.configProviderWith(_.nested("webhook").nested("twitch")
      .load[TwitchWebHookConfig]
    )
  )
}
